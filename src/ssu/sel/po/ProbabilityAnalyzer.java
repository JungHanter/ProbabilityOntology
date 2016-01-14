package ssu.sel.po;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;

import ssu.sel.po.model.*;
import ssu.sel.po.utils.*;

import java.io.InputStream;
import java.math.*;
import java.lang.*;
import java.util.*;

/**
 * Created by hanter on 2016. 1. 6..
 */
public class ProbabilityAnalyzer {
    public static final String PROB_ONTOLOYY = "ProbabilityOntology/data/ProbOnt.owl";
    public static final String PROB_ONT_NAMESPACE = "http://soft.ssu.ac.kr/ontology/probability_ontology#";

    private String prefixString =
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX : <http://soft.ssu.ac.kr/ontology/probability_ontology#>\n" +
            "PREFIX base: <http://soft.ssu.ac.kr/ontology/probability_ontology#>\n" +
            "PREFIX pront: <http://soft.ssu.ac.kr/ontology/probability_ontology#>\n";

    private double minSupport = 0.25;
    private double minConfidence = 0.5;

    private Set<String> prefixNameSet = new HashSet<String>();

    private final OntModel probOntModel;
    private OntModel alignedSchemaOntModel;
    private OntModel alignedDataOntModel;

    private InfModel alignedInfModel;
    private InfModel associationInfModel;

    public boolean addAlignmentSchemaOntology(String ontFile, String baseNamespace, String qname) {
        return addAlignmentSchemaOntology(ontFile, baseNamespace, qname, "");
    }
    public boolean addAlignmentSchemaOntology(String ontFile, String baseNamespace, String qname, String language) {
        Model model = loadOntology(ontFile, baseNamespace, language);
        return addAlignmentSchemaOntology(model, baseNamespace, qname);
    }

    public boolean addAlignmentSchemaOntology(Model model, String baseNamespace, String qname) {
        if(model == null) return false;
        alignedSchemaOntModel.add(model);

        if(!prefixNameSet.contains(qname)) {
            prefixNameSet.add(qname);
            addPrefix(baseNamespace, qname);
        }
        return true;
    }

    public boolean addAlignmentDataOntology(String ontFile, String baseNamespace, String qname) {
        return addAlignmentDataOntology(ontFile, baseNamespace, qname, "");
    }
    public boolean addAlignmentDataOntology(String ontFile, String baseNamespace, String qname, String language) {
        Model model = loadOntology(ontFile, baseNamespace, language);
        return addAlignmentDataOntology(model, baseNamespace, qname);
    }

    public boolean addAlignmentDataOntology(Model model, String baseNamespace, String qname) {
        if(model == null) return false;
        alignedDataOntModel.add(model);

        if(!prefixNameSet.contains(qname)) {
            prefixNameSet.add(qname);
            addPrefix(baseNamespace, qname);
        }
        return true;
    }

    public void addAnalyzingAssociation(String namespace, String id) {

    }

    //TODO to change definition and implementation
    public void analyze() {
        findAssociations();
    }

    public List<Triple> findAssociations() {
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
//        Reasoner reasoner = PelletReasonerFactory.theInstance().create();
        reasoner.bindSchema(alignedSchemaOntModel);
        alignedInfModel = ModelFactory.createInfModel(reasoner, alignedDataOntModel);

//        ValidityReport report = alignedSchemaOntModel.validate();
//        printIterator(report.getReports(), "alignedSchemaOntModel");
//        report = alignedDataOntModel.validate();
//        printIterator(report.getReports(), "Validation Results for alignedDataOntModel");
//        report = alignedInfModel.validate();
//        printIterator(report.getReports(), "Validation Results for alignedInfModel");

        QueryExecution exec = null;
        ResultSet rs = null;

        //1. Find Class Type of Object of Target Association Relationship
        Map<RDFNode, List<RDFNode>> targetListMap = new HashMap<>();    //targetListType(Class), targetList(Individual)

        RDFNode lastTargetType = null;
        List<RDFNode> nowTargetList = null;

        exec = runQuery("SELECT DISTINCT ?directType ?target " +
                "WHERE { " +
                "  ?targetRelationship a pront:AssociationTargetRelationship . " +
                "  ?targetRelationship pront:isAssociationObject ?target . " +
                "  ?target rdf:type ?directType . " +
                "  FILTER NOT EXISTS {" +
                "    ?object rdf:type ?type . " +
                "    ?type rdfs:subClassOf ?directType . " +
                "    FILTER NOT EXISTS {" +
                "      ?type owl:equivalentClass ?directType . " +
                "      FILTER (NOT EXISTS {?type owl:equivalentClass owl:NamedIndividual . }) "+
                "    }" +
                "  }" +
                "} ORDER BY ?directType", alignedInfModel);
        rs = exec.execSelect();
        while (rs.hasNext()) {
            QuerySolution soln = rs.nextSolution();
            RDFNode type = soln.get("directType");
            RDFNode target = soln.get("target");

            if(!type.equals(lastTargetType)) {
                lastTargetType = type;
                nowTargetList = new ArrayList<>();
                targetListMap.put(type, nowTargetList);
            }

            nowTargetList.add(soln.get("?target"));
        }
        exec.close();

        //2. Retrieve all related association data for each target and make combination
        Map<RDFNode, Set<Set<AssociationObjectRDFNode>>> combinationSets = new HashMap<>();   //targetNode(Individual/Category), associationSetsForTargetNode
        Map<RDFNode, DataRange> rdfNodeDataRangeMap = new HashMap<>(); //nodeType, datarnage

        //TODO to make combinationSets for each targetListType. But now, it's only for one target list type
        for (List<RDFNode> targetList : targetListMap.values()) {
            /*** targetListMap.key => target Type(class)
             *   targetListMap.values => target categries value(individual) ***/
            for (RDFNode targetCategory : targetList) {
                Set<Set<AssociationObjectRDFNode>> setsForTarget = new HashSet<>();
                combinationSets.put(targetCategory, setsForTarget);

                runQueryAndPrint("SELECT DISTINCT ?subject ?directType ?object ?data " +
                        "WHERE {" +
                        "  ?targetRelationship a pront:AssociationTargetRelationship . " + //added
                        "  ?targetRelationship pront:isAssociationObject <" + targetCategory.toString() + "> . " +
                        "  ?targetRelationship pront:isAssociationSubject ?subject . " +
                        "  ?associationRelationship a pront:AssociationRelationship . " + //added
                        "  FILTER (NOT EXISTS {?associationRelationship a pront:AnalysisTargetRelationship . }) " + //added
                        "  ?associationRelationship pront:isAssociationSubject ?subject . " +
                        "  ?associationRelationship pront:isAssociationObject ?object . " +
                        "  OPTIONAL { ?object pront:dataOfAssociationObject ?data . } " +
                        "  ?object rdf:type ?directType . " +
                        "  FILTER NOT EXISTS {" +
                        "    ?object rdf:type ?type . " +
                        "    ?type rdfs:subClassOf ?directType . " +
                        "    FILTER NOT EXISTS {" +
                        "      ?type owl:equivalentClass ?directType . " +
                        "      FILTER (NOT EXISTS {?type owl:equivalentClass owl:NamedIndividual . }) " +
                        "    }" +
                        "  }" +
                        "} ORDER BY ?subject", alignedInfModel);

                exec = runQuery("SELECT DISTINCT ?subject ?directType ?object ?data " +
                        "WHERE {" +
                        "  ?targetRelationship pront:isAssociationObject <" + targetCategory.toString() + "> . " +
                        "  ?targetRelationship pront:isAssociationSubject ?subject . " +
                        "  ?associationRelationship a pront:AssociationRelationship . " +
                        "  ?associationRelationship pront:isAssociationSubject ?subject . " +
                        "  ?associationRelationship pront:isAssociationObject ?object . " +
                        "  OPTIONAL { ?object pront:dataOfAssociationObject ?data . } " +
                        "  ?object rdf:type ?directType . " +
                        "  FILTER NOT EXISTS {" +
                        "    ?object rdf:type ?type . " +
                        "    ?type rdfs:subClassOf ?directType . " +
                        "    FILTER NOT EXISTS {" +
                        "      ?type owl:equivalentClass ?directType . " +
                        "      FILTER (NOT EXISTS {?type owl:equivalentClass owl:NamedIndividual . }) " +
                        "    }" +
                        "  }" +
                        "} ORDER BY ?subject", alignedInfModel);

                rs = exec.execSelect();
                RDFNode lastSubj = null;
                Set<AssociationObjectRDFNode> nowSet = null;
                while (rs.hasNext()) {
                    QuerySolution soln = rs.nextSolution();
                    RDFNode subj = soln.get("?subject");
                    RDFNode obj = soln.get("?object");
                    RDFNode type = soln.get("?directType");
                    RDFNode data = soln.get("?data");

                    //add related association data and metadata
                    if (!subj.equals(lastSubj)) {
                        lastSubj = subj;
                        nowSet = new HashSet<>();
                        setsForTarget.add(nowSet);
                    }
                    if (!targetCategory.equals(obj)) {
                        if(data == null) nowSet.add(new AssociationObjectRDFNode(obj, type));
                        else nowSet.add(new AssociationObjectRDFNode(obj, type, (Literal)data));
                    }

                    //add and update datarange
                    addDataRange(rdfNodeDataRangeMap, obj, type, data);
                }
            }
        }

        //3. Find Association Combinations
        Map<Set<RDFNode>, Integer> assCombSetMap = findAssociationCombinationWithCount(combinationSets);
        int totalCount = getAssociationCombinationTotalCount(assCombSetMap);
        Set<RDFNode> targetTypeSet = getAllTypeSet(combinationSets);
        Map<Integer, Set<PossibleSet>> possibleSetMap = generatePossibleAssociationSet2(targetTypeSet, assCombSetMap);

        //4. Filter PossibleSet Combinations Satisfying Minimum Support
        filterPossibleSet(possibleSetMap);

        //5. Analyze Association Probability
        //TODO temporary
        Set<AssociationAnalyzer> analyzerSet = assortAssociations(combinationSets, rdfNodeDataRangeMap, possibleSetMap);

        /****** For test ******/
        Set<RDFNode> aaa = null;
        Set<AssociationObjectRDFNode> bbb = null;
        for (RDFNode targetNode : combinationSets.keySet()) {
            Set<Set<AssociationObjectRDFNode>> assSets = combinationSets.get(targetNode);
            for (Set<AssociationObjectRDFNode> associationSet : assSets) {
                Set<AssociationObjectRDFNode> numericSet = new HashSet<>();
                Set<AssociationObjectRDFNode> nonNumericSet = new HashSet<>();

                for (AssociationObjectRDFNode assNode : associationSet) {
                    LiteralValueType valueType = rdfNodeDataRangeMap.get(assNode.getRdfType()).getValueType();

                    if(valueType == LiteralValueType.Double ||
                            valueType == LiteralValueType.Integer) {
                        numericSet.add(assNode);
                    } else {
                        nonNumericSet.add(assNode);
                    }
                }
                aaa = AssociationObjectRDFNode.getTypeSet(associationSet);
                bbb = nonNumericSet;
                break;
            }
            break;
        }

        for (AssociationAnalyzer analyzer : analyzerSet) {
            Set<Set<AssociationObjectRDFNode>> foundSet = analyzer.findNumericValueSets(aaa, bbb);
            System.out.println(foundSet);
        }
        /****** end test ******/

        return null; //TODO
    }

    private void addDataRange(Map<RDFNode, DataRange> dataRangeMap,
                              RDFNode obj, RDFNode type, RDFNode data) {
        DataRange dataRange;
        if(data != null) {
            if (dataRangeMap.keySet().contains(type)) {
                dataRange = dataRangeMap.get(type);
                switch (dataRange.getValueType()) {
                    case Integer:
                        dataRange.addValue(((Literal)data).getInt());
                        break;
                    case Double:
                        dataRange.addValue(((Literal)data).getDouble());
                        break;
                    case String:
                        dataRange.addValue(((Literal)data).getString());
                        break;
                    case Literal:
                    case RDFNode:
                    default:
                        dataRange.addValue(data);
                        break;

                }
            } else {
                if (data.isLiteral()) {
                    Literal literalData = (Literal) data;
                    RDFDatatype literalType = literalData.getDatatype();
                    Class literalTypeClass = literalType.getJavaClass();

                    if (literalTypeClass.equals(Integer.class) ||
                            literalTypeClass.equals(BigInteger.class) ||
                            literalTypeClass.equals(Long.class) ||
                            literalTypeClass.equals(Short.class)) {
                        dataRange = new NumericDataRange<Integer>(LiteralValueType.Integer);
                        dataRange.addValue(literalData.getInt());
                    } else if (literalTypeClass.equals(Double.class) ||
                            literalTypeClass.equals(Float.class)) {
                        dataRange = new NumericDataRange<Integer>(LiteralValueType.Double);
                        dataRange.addValue(literalData.getDouble());
                    } else if (literalTypeClass.equals(String.class)) {
                        dataRange = new DataRange<String>(LiteralValueType.String);
                        dataRange.addValue(literalData.getString());
                    } else {
                        dataRange = new DataRange<Literal>(LiteralValueType.Literal);
                        dataRange.addValue(literalData);
                    }
                } else {
                    dataRange = new DataRange<RDFNode>(LiteralValueType.RDFNode);
                    dataRange.addValue(data);
                }
                dataRangeMap.put(type, dataRange);
            }
        } else {
            if (dataRangeMap.keySet().contains(type)) {
                dataRange = dataRangeMap.get(type);
            } else {
                dataRange = new DataRange<RDFNode>(LiteralValueType.Self);
                dataRangeMap.put(type, dataRange);
            }
            dataRange.addValue(obj);
        }
    }

    private Set<Set<RDFNode>> findAssociationCombination (Map<RDFNode, Set<Set<AssociationObjectRDFNode>>> combinationSets) {
        Set<Set<RDFNode>> assCombSets = new HashSet<>();

        for(Set<Set<AssociationObjectRDFNode>> combSets : combinationSets.values()) {
            for(Set<AssociationObjectRDFNode> combSet : combSets) {
                Set<RDFNode> typeSet = getTypeSet(combSet);
                assCombSets.add(typeSet);
            }
        }
        return assCombSets;
    }

    private Map<Set<RDFNode>, Integer> findAssociationCombinationWithCount (Map<RDFNode, Set<Set<AssociationObjectRDFNode>>> combinationSets) {
        Map<Set<RDFNode>, Integer> assCombSetMap = new HashMap<>();

        for(Set<Set<AssociationObjectRDFNode>> combSets : combinationSets.values()) {
            for(Set<AssociationObjectRDFNode> combSet : combSets) {
                Set<RDFNode> typeSet = getTypeSet(combSet);

                if (assCombSetMap.keySet().contains((typeSet))) {
                    assCombSetMap.put(typeSet, assCombSetMap.get(typeSet) + 1);
                } else {
                    assCombSetMap.put(typeSet, 1);
                }
            }
        }
        return assCombSetMap;
    }

    private int getAssociationCombinationTotalCount(Map<Set<RDFNode>, Integer> assCombSetMap) {
        int count = 0;
        for (Set<RDFNode> set : assCombSetMap.keySet()) {
            count += assCombSetMap.get(set);
        }
        return count;
    }

    private Set<RDFNode> getAllTypeSet (Map<RDFNode, Set<Set<AssociationObjectRDFNode>>> combinationSets) {
        Set<RDFNode> typeSet = new HashSet<>();

        for(Set<Set<AssociationObjectRDFNode>> combSets : combinationSets.values()) {
            for(Set<AssociationObjectRDFNode> combSet : combSets) {
                for(AssociationObjectRDFNode nt : combSet) {
                    typeSet.add(nt.getRdfType());
                }
            }
        }
        return typeSet;
    }

    private Set<RDFNode> getTypeSet(Set<AssociationObjectRDFNode> nodeAndTypeSet) {
        Set<RDFNode> typeSet = new HashSet<>();
        for(AssociationObjectRDFNode nodeAndType : nodeAndTypeSet) {
            typeSet.add(nodeAndType.getRdfType());
        }
        return typeSet;
    }

    private Map<Integer, Set<Set<RDFNode>>> generatePossibleAssociationSet(Set<RDFNode> set) {
        Map<Integer, Set<Set<RDFNode>>> possibleSetMap = new HashMap<>();   //size, possibleSet

        Object[] setArray = set.toArray();
        int size = setArray.length;

        for(int nowSize=1; nowSize<=size; nowSize++) {
//            Set<Set<RDFNode>> possibleSets = new HashSet<>();
            Set<Set<RDFNode>> possibleSets = Combination.combination(setArray, nowSize);
            possibleSetMap.put(nowSize, possibleSets);
        }

        return possibleSetMap;
    }

    private Map<Integer, Set<PossibleSet>> generatePossibleAssociationSet2(Set<RDFNode> set, Map<Set<RDFNode>, Integer> assCombSetMap) {
        int totalSize = getAssociationCombinationTotalCount(assCombSetMap);
        Map<Integer, Set<PossibleSet>> possibleSetMap = new HashMap<>();   //size, possibleSet

        Object[] setArray = set.toArray();
        int size = setArray.length;

        for(int nowSize=1; nowSize<=size; nowSize++) {
            Set<PossibleSet> possibleSets = new HashSet<>();
            possibleSetMap.put(nowSize, possibleSets);

            Set<Set<RDFNode>> possibleCombSets = Combination.combination(setArray, nowSize);
            for (Set<RDFNode> possibleCombSet : possibleCombSets) {
                int cnt = findAppearCountPossibleSet(possibleCombSet, assCombSetMap);
                PossibleSet possibleSet = new PossibleSet(possibleCombSet, cnt, cnt*1.0/totalSize);
                possibleSets.add(possibleSet);
            }
        }

        return possibleSetMap;
    }

    private int findAppearCountPossibleSet(Set<RDFNode> possibleSet, Map<Set<RDFNode>, Integer> assCombSetMap) {
        int findAppearCount = 0;
        for (Set<RDFNode> assCombSet : assCombSetMap.keySet()) {
            if(assCombSet.containsAll(possibleSet)) {
                findAppearCount += assCombSetMap.get(assCombSet);
            }
        }
        return findAppearCount;
    }

    private void filterPossibleSet(Map<Integer, Set<PossibleSet>> possibleSetMap) {
        for(Integer i : possibleSetMap.keySet()) {
            Set<PossibleSet> sets = possibleSetMap.get(i);
            Set<PossibleSet> removingSets = new HashSet<>();
            for(PossibleSet possibleSet : sets) {
                if(possibleSet.getSupport() < minSupport) {
//                    sets.remove(possibleSet);
                    removingSets.add(possibleSet);
                }
            }
            sets.removeAll(removingSets);
        }
    }

    // 1. Collecting and Distributing among value sets
    private Set<AssociationAnalyzer> assortAssociations(Map<RDFNode, Set<Set<AssociationObjectRDFNode>>> combinationSets,
                                                        Map<RDFNode, DataRange> dataRangeMap,
                                                        Map<Integer, Set<PossibleSet>> possibleSetMap) {
        Set<AssociationAnalyzer> analyzerSet = new HashSet<AssociationAnalyzer>();

        for (RDFNode targetNode : combinationSets.keySet()) {   //each individuals as targets ex)Hypotension (Category)
            Set<Set<AssociationObjectRDFNode>> associationSets = combinationSets.get(targetNode);
            Map<Set<AssociationObjectRDFNode>, Map<Set<RDFNode>,Set<Set<AssociationObjectRDFNode>>>> valueSet = new HashMap<>();

            for (Set<AssociationObjectRDFNode> associationSet : associationSets) {
                for (Set<PossibleSet> tempPossibleSets : possibleSetMap.values())
                    for (PossibleSet possibleSet : tempPossibleSets) {
                        Set<RDFNode> possibleRDFNodeSet = possibleSet.getPossibleSet();

                        if(AssociationObjectRDFNode.containsAllType(associationSet, possibleRDFNodeSet)) {
                            //makeValueSet for each possibleSet
                            Set<AssociationObjectRDFNode> numericSet = new HashSet<>();
                            Set<AssociationObjectRDFNode> nonNumericSet = new HashSet<>();

                            for (AssociationObjectRDFNode assNode : associationSet) {
                                if(possibleRDFNodeSet.contains(assNode.getRdfType())) {
                                    LiteralValueType valueType = dataRangeMap.get(assNode.getRdfType()).getValueType();

                                    if(valueType == LiteralValueType.Double ||
                                            valueType == LiteralValueType.Integer) {
                                        numericSet.add(assNode);
                                    } else {
                                        nonNumericSet.add(assNode);
                                    }
                                }
                            }

                            Map<Set<RDFNode>,Set<Set<AssociationObjectRDFNode>>> numericValueSetsMap;
                            if (valueSet.keySet().contains(nonNumericSet)) {
                                numericValueSetsMap = valueSet.get(nonNumericSet);
                            } else {
                                numericValueSetsMap = new HashMap<>();
                                valueSet.put(nonNumericSet, numericValueSetsMap);
                            }

                            Set<RDFNode> numericTypeSet = AssociationObjectRDFNode.getTypeSet(numericSet);
                            Set<Set<AssociationObjectRDFNode>> numericValueSets;
                            if (numericValueSetsMap.keySet().contains(numericTypeSet)) {
                                numericValueSets = numericValueSetsMap.get(numericTypeSet);
                            } else {
                                numericValueSets = new HashSet<>();
                                numericValueSetsMap.put(numericTypeSet, numericValueSets);
                            }
                            numericValueSets.add(numericSet);
                        }
                    }
            }

            AssociationAnalyzer resultAnalyzer = new AssociationAnalyzer(targetNode, dataRangeMap, valueSet);
            analyzerSet.add(resultAnalyzer);
        }
        return analyzerSet;
    }


    public QueryExecution runQuery(String queryReq) {
        return runQuery(queryReq, alignedInfModel);
    }

    public QueryExecution runQuery(String queryReq, Model model) {
        StringBuffer queryStr = new StringBuffer();
        queryStr.append(prefixString);
        queryStr.append(queryReq);

        Query query = QueryFactory.create(queryStr.toString());
        QueryExecution qexec = QueryExecutionFactory.create(query, model);

        return qexec;
    }

    //for test
    public void runQueryAndPrint(String queryReq) {
        runQueryAndPrint(queryReq, alignedInfModel);
    }

    public void runQueryAndPrint(String queryReq, Model model) {
        StringBuffer queryStr = new StringBuffer();
        queryStr.append(prefixString);
        queryStr.append(queryReq);

        Query query = QueryFactory.create(queryStr.toString());
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try {
            ResultSet rs = qexec.execSelect();
            List<String> vars = rs.getResultVars();

            for(String var : vars) {
                System.out.print(var);
                System.out.print('\t');
            }
            System.out.println();

            while( rs.hasNext()){
                QuerySolution soln = rs.nextSolution();

                for(String var : vars) {
                    RDFNode node = soln.get(var);
                    if(node == null) System.out.print("NULL");
                    else System.out.print(node.toString());
                    System.out.print('\t');
                }
                System.out.println();
            }
        } finally {
            System.out.println();
            qexec.close();
        }
    }

    private void addPrefix(String baseNamespace, String qname) {
        prefixString += "PREFIX " + qname + ": <" + baseNamespace + ">\n";
    }

    public ProbabilityAnalyzer() {
        probOntModel = loadOntology(PROB_ONTOLOYY, PROB_ONT_NAMESPACE, "RDF/XML");

        alignedSchemaOntModel = ModelFactory.createOntologyModel();
        alignedSchemaOntModel.add(probOntModel);

        alignedDataOntModel = ModelFactory.createOntologyModel();
    }

    private OntModel loadOntology(String ontFile, String baseNamespace) {
        return loadOntology(ontFile, baseNamespace, "");
    }
    private OntModel loadOntology(String ontFile, String baseNamespace, String language) {
        OntModel model = ModelFactory.createOntologyModel();
        InputStream isOnt = null;
        isOnt = FileManager.get().open(ontFile);

        try {
            if ("".equals(language)) model.read(isOnt, baseNamespace);
            else model.read(isOnt, baseNamespace, language);
        } catch (Exception e) {
            e.printStackTrace();
            if(isOnt != null) {
                try{ isOnt.close(); } catch (Exception e2){;}
            }
            return null;
        }

        return model;
    }


    public static void printIterator(Iterator i, String header) {
        System.out.println(header);
        for(int c = 0; c < header.length(); c++)
            System.out.print("=");
        System.out.println();

        if(i.hasNext()) {
            while (i.hasNext())
                System.out.println( i.next() );
        } else
            System.out.println("<EMPTY>");
        System.out.println();

    }
}
