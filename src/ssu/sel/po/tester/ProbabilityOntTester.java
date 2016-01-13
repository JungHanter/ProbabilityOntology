package ssu.sel.po.tester;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import ssu.sel.po.ProbabilityAnalyzer;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;

/**
 * Created by hanter on 2016. 1. 6..
 */
public class ProbabilityOntTester {
    public static final String HEALTH_CONTEXT_ONTOLOGY = "ProbabilityOntology/data/HealthContextAligned.owl";
    public static final String HCTX_ONT_NAMESPACE = "http://soft.ssu.ac.kr/ontology/health_context#";

    public static void main(String[] args) {
        ProbabilityAnalyzer analyzer = new ProbabilityAnalyzer();

        analyzer.addAlignmentSchemaOntology(HEALTH_CONTEXT_ONTOLOGY, HCTX_ONT_NAMESPACE, "hctx", "RDF/XML");
        analyzer.addAlignmentDataOntology(HEALTH_CONTEXT_ONTOLOGY, HCTX_ONT_NAMESPACE, "hctx", "RDF/XML");

        analyzer.analyze();



        /*analyzer.runQueryAndPrint("SELECT DISTINCT ?objectClass \n" +
                "WHERE {\n" +
                "  ?association a pront:AssociationTargetRelationship . \n" +
                "  ?association pront:associationObject ?obj . \n" +
                "  ?obj a ?objectClass" +
                "}");

        analyzer.runQueryAndPrint("SELECT DISTINCT ?subjectClass \n" +
                "WHERE {\n" +
                "  ?association a pront:AssociationTargetRelationship . \n" +
                "  ?association pront:associationSubject ?subj . \n" +
                "  ?subj a ?subjectClass" +
                "}");

        analyzer.runQueryAndPrint("SELECT DISTINCT ?subj ?association \n" +
                "WHERE {\n" +
                "  ?association a pront:AssociationTargetRelationship . \n" +
                "  ?association pront:associationSubject ?subj" +
                "} ORDER BY ?subj");*/
    }


}
