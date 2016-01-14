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
    final private RDFNode targetNode;   //targetIndividaul (Category)
    final private Map<RDFNode, DataRange> dataRangeMap;
    final private Map<Integer, Set<PossibleSet>> possibleSetMap;
    final private Map<Set<AssociationObjectRDFNode>, Map<Set<RDFNode>,Set<Set<AssociationObjectRDFNode>>>> valueSet;

    public AssociationAnalyzer(RDFNode targetNode,
                               Map<RDFNode, DataRange> dataRangeMap,
                               Map<Integer, Set<PossibleSet>> possibleSetMap,
                               Map<Set<AssociationObjectRDFNode>, Map<Set<RDFNode>,Set<Set<AssociationObjectRDFNode>>>> valueSet) {
        this.targetNode = targetNode;
        this.dataRangeMap = dataRangeMap;
        this.possibleSetMap = possibleSetMap;
        this.valueSet = valueSet;
    }

    public double analyzeAssociationProbability(Set<AssociationObjectRDFNode> associationSet) {
        Set<RDFNode> typeSet = AssociationObjectRDFNode.getTypeSet(associationSet);

        PossibleSet possibleSet = findPossibleSet(typeSet);
        if(possibleSet == null) {
            possibleSet = findOptimalPossibleSubSet(typeSet);
            if (possibleSet == null) return 0.0;

            //change to most approximal subset
            typeSet = possibleSet.getPossibleSet();
            associationSet = subSet(associationSet, typeSet);
        }

        Set<AssociationObjectRDFNode> numericSet = new HashSet<>();
        Set<AssociationObjectRDFNode> nonNumericSet = new HashSet<>();
        for (AssociationObjectRDFNode assNode : associationSet) {
            LiteralValueType valueType = dataRangeMap.get(assNode.getRdfType()).getValueType();

            if(valueType == LiteralValueType.Double ||
                    valueType == LiteralValueType.Integer) {
                numericSet.add(assNode);
            } else {
                nonNumericSet.add(assNode);
            }
        }

        Set<RDFNode> numericTypeSet = getNumericTypeSet(typeSet);
        Set<RDFNode> nonNumericTypeSet = getNonNumericTypeSet(typeSet);

        Set<Set<AssociationObjectRDFNode>> numericValueSets = findNumericValueSets(numericTypeSet, nonNumericTypeSet, nonNumericSet);
        if(numericValueSets == null) return 0.0;   //TODO do analyzing with subset. to be implemented.

        double probability = 1.0;
        double minDifference = 0.0;
        Set<AssociationObjectRDFNode> minDifferentSet = null;
        for (Set<AssociationObjectRDFNode> numericValueSet : numericValueSets) {
            double nowProb = 1.0;
            double nowDifference = 0.0;

            for(RDFNode numericType : numericTypeSet) {
                AssociationObjectRDFNode analysisNode = getRDFNode(numericSet, numericType);        //my node
                AssociationObjectRDFNode valueSetNode = getRDFNode(numericValueSet, numericType);   //base node;

                double valueSetNodeVal = valueSetNode.getDataDouble();
                double analysisNodeVal = analysisNode.getDataDouble();

                double minVal = ((NumericDataRange)dataRangeMap.get(numericType)).getMin().doubleValue();
                double maxVal = ((NumericDataRange)dataRangeMap.get(numericType)).getMax().doubleValue();

                if (analysisNodeVal < minVal) analysisNodeVal = minVal;
                else if (analysisNodeVal > maxVal) analysisNodeVal = maxVal;

                if (minVal == maxVal) {
                    //nowDifference += 0.0;
                    //nowProb *= 1.0;
                } else {
                    double valueSetRelativePos = (valueSetNodeVal - minVal) / (maxVal - minVal);
                    double analysisRelativePos = (analysisNodeVal - minVal) / (maxVal - minVal);
                    double difference = Math.abs(valueSetRelativePos - analysisRelativePos);

                    nowDifference += difference;
                    if (analysisNodeVal == valueSetNodeVal) {
                        nowProb *= 1.0;
                    } else if (analysisNodeVal < valueSetNodeVal) {
                        double ratio = (valueSetNodeVal - analysisNodeVal) / (valueSetNodeVal - minVal);
                        nowProb *= (1-ratio);
                    } else { //analysisNodeVal > valueSetNodeVal
                        double ratio = (analysisNodeVal - valueSetNodeVal) / (maxVal - valueSetNodeVal);
                        nowProb *= (1-ratio);
                    }
                }

            }

            if(minDifferentSet == null) {
                minDifference = nowDifference;
                minDifferentSet = numericSet;
                probability = nowProb;
            } else {
                if(nowDifference < minDifference) {
                    minDifference = nowDifference;
                    minDifferentSet = numericValueSet;
                    probability = nowProb;
                }
            }
        }

        return probability * possibleSet.getSupport();
    }

    private AssociationObjectRDFNode getRDFNode(Set<AssociationObjectRDFNode> set, RDFNode type) {
        for (AssociationObjectRDFNode node : set) {
            if (node.getRdfType().equals(type)) return node;
        }
        return null;
    }

    /*public Set<Set<AssociationObjectRDFNode>> findNumericValueSets(Set<RDFNode> findingTypeSet,
                                                                    Set<AssociationObjectRDFNode> findingNonNumericSet) {
        Set<RDFNode> findingNumericTypeSet = getNumericTypeSet(findingTypeSet);
        Set<RDFNode> findingNonNumericTypeSet = getNonNumericTypeSet(findingTypeSet);*/
    private Set<Set<AssociationObjectRDFNode>> findNumericValueSets(Set<RDFNode> findingNumericTypeSet,
                                                                   Set<RDFNode> findingNonNumericTypeSet,
                                                                   Set<AssociationObjectRDFNode> findingNonNumericSet) {
        if(!findingNonNumericTypeSet.equals(AssociationObjectRDFNode.getTypeSet(findingNonNumericSet))) {
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

    private PossibleSet findPossibleSet(Set<RDFNode> typeSet) {
        Set<PossibleSet> sizedPossibleSets = possibleSetMap.get(typeSet.size());

        for (PossibleSet possibleSet : sizedPossibleSets) {
            if (possibleSet.getPossibleSet().equals(typeSet))
                return possibleSet;
        }
        return null;
    }

    private PossibleSet findOptimalPossibleSubSet(Set<RDFNode> typeSet) {
        int typeSetSize = typeSet.size();
        if (typeSetSize <= 1) return null;

        for (int size=typeSetSize-1; size>=1; size--) {
            Set<PossibleSet> sizedPossibleSets = possibleSetMap.get(size);
            Set<Set<RDFNode>> subSets = subSet(typeSet, size);

            PossibleSet selectedPossibleSet = null;
            for (PossibleSet possibleSet : sizedPossibleSets) {
                PossibleSet nowPossibleSet = null;
                for (Set<RDFNode> subset : subSets) {
                    if (possibleSet.getPossibleSet().equals(subset)) {
                        nowPossibleSet = possibleSet;
                        break;
                    }
                }

                if (nowPossibleSet != null) {
                    if (selectedPossibleSet == null)
                        selectedPossibleSet = nowPossibleSet;
                    else {
                        if (selectedPossibleSet.getSupport() < nowPossibleSet.getSupport())
                            selectedPossibleSet = nowPossibleSet;
                    }
                }
            }

            if (selectedPossibleSet != null) return selectedPossibleSet;
        }
        return null;
    }

    private Set<Set<RDFNode>> subSet(Set<RDFNode> subSet, int subSetSize) {
        return Combination.combination(subSet.toArray(), subSetSize);
    }

    private Set<AssociationObjectRDFNode> subSet(Set<AssociationObjectRDFNode> origianlSet, Set<RDFNode> subTypeSet) {
        Set<AssociationObjectRDFNode> subset = new HashSet<>();
        for (AssociationObjectRDFNode node : origianlSet) {
            if (subTypeSet.contains(node.getRdfType())) subset.add(node);
        }
        return subset;
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

    public RDFNode getTargetNode() {
        return targetNode;
    }
}
