package ssu.sel.po;

import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.RDFNode;

import ssu.sel.po.model.*;
import ssu.sel.po.utils.*;

import java.util.*;

/**
 * Created by hanter on 2016. 1. 11..
 */
public class AssociationAnalyzer {
    final private RDFNode targetNode;
    final private Map<RDFNode, DataRange> dataRangeMap;
    final private Map<Set<AssociationObjectRDFNode>, Map<Set<RDFNode>,Set<Set<AssociationObjectRDFNode>>>> valueSet;

    public AssociationAnalyzer(RDFNode targetNode,
                               Map<RDFNode, DataRange> dataRangeMap,
                               Map<Set<AssociationObjectRDFNode>, Map<Set<RDFNode>,Set<Set<AssociationObjectRDFNode>>>> valueSet) {
        this.targetNode = targetNode;
        this.dataRangeMap = dataRangeMap;
        this.valueSet = valueSet;
    }

    public Set<Triple> analyzeAssociationProbability(RDFNode probabilitySubject,
                                                     Set<AssociationObjectRDFNode> associationSet) {
        return null;
    }

    public Set<Set<AssociationObjectRDFNode>> findNumericValueSets(Set<RDFNode> findingTypeSet,
                                                                    Set<AssociationObjectRDFNode> findingNonNumericSet) {
        Set<RDFNode> findingNumericTypeSet = getNumericTypeSet(findingTypeSet);
        Set<RDFNode> findingNonNumericTypeSet = getNonNumericTypeSet(findingTypeSet);

        if(!findingNonNumericSet.equals(AssociationObjectRDFNode.getTypeSet(findingNonNumericSet))) {
            return null;
        }

        for (Set<AssociationObjectRDFNode> nonNumericSet : valueSet.keySet()) {
            if (AssociationObjectRDFNode.getTypeSet(nonNumericSet).equals(findingNonNumericTypeSet)) {
                Map<Set<RDFNode>,Set<Set<AssociationObjectRDFNode>>> numericValueSetsMap = valueSet.get(nonNumericSet);
                if(numericValueSetsMap == null) return null;
                Set<Set<AssociationObjectRDFNode>> numericValueSets = numericValueSetsMap.get(findingNumericTypeSet);
                return numericValueSets;
            }
        }
        return null;
    }

    private Set<RDFNode> getNumericTypeSet(Set<RDFNode> typeSet) {
        Set<RDFNode> numericTypeSet = new HashSet<>();

        for(RDFNode type : typeSet) {
            LiteralValueType valueType = dataRangeMap.get(type).getValueType();
            if(valueType == LiteralValueType.Double ||
                    valueType == LiteralValueType.Integer) {
                numericTypeSet.add(type);
            }
        }
        return numericTypeSet;
    }

    private Set<RDFNode> getNonNumericTypeSet(Set<RDFNode> typeSet) {
        Set<RDFNode> nonNumericTypeSet = new HashSet<>();

        for(RDFNode type : typeSet) {
            LiteralValueType valueType = dataRangeMap.get(type).getValueType();
            if(valueType != LiteralValueType.Double &&
                    valueType != LiteralValueType.Integer) {
                nonNumericTypeSet.add(type);
            }
        }
        return nonNumericTypeSet;
    }
}
