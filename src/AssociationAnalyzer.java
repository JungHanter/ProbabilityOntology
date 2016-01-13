import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import ssu.sel.po.model.AssociationObjectRDFNode;

import java.util.*;

/**
 * Created by hanter on 2016. 1. 11..
 */
public class AssociationAnalyzer {
    private RDFNode target;
    private Map<RDFNode, NodeValues> valueListForAssNode;
//    Map<Set<RDFNode>>, Set<>

    public AssociationAnalyzer(RDFNode target, Set<Set<AssociationObjectRDFNode>> associationSets) {

    }

    private void arangeSets(Set<Set<AssociationObjectRDFNode>> associationSets) {
        for(Set<AssociationObjectRDFNode> assSet : associationSets) {

        }
    }

    private class NodeValues<T> {
        RDFNode Node;
        List<T> values;
        int valueType;

        public RDFNode getNode() {
            return Node;
        }

        public List<T> getValues() {
            return values;
        }

        public T getValue(int index) {
            return values.get(index);
        }
    }


    private enum ValueType {
        String, Integer, Double, RDFNode, Null
    }
}
