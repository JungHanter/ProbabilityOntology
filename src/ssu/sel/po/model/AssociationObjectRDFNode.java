package ssu.sel.po.model;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;

import java.util.*;

/**
 * Created by hanter on 2016. 1. 8..
 */
public class AssociationObjectRDFNode {
    final RDFNode rdfNode;
    final RDFNode rdfType;
    final Literal data;

    public AssociationObjectRDFNode(RDFNode rdfNode, RDFNode rdfType) {
        this(rdfNode, rdfType, null);
    }

    public AssociationObjectRDFNode(RDFNode rdfNode, RDFNode rdfType, Literal data) {
        this.rdfNode = rdfNode;
        this.rdfType = rdfType;
        this.data = data;
    }

    public RDFNode getRdfNode() {
        return rdfNode;
    }

    public RDFNode getRdfType() {
        return rdfType;
    }

    public  boolean hasData() {
        return (data != null);
    }

    public Literal getDataRDF() {
        return data;
    }

    public int getDataInteger() {
        return data.getInt();
    }

    public double getDataDouble() {
        return data.getDouble();
    }

    public String getDataString() {
        return data.getString();
    }

    @Override
    public String toString() {
        return rdfNode.toString() + " " + rdfType.toString();
    }

    @Override
    public boolean equals(Object obj) {
//        if(obj.getClass() == AssociationObjectRDFNode.class) {
        if(obj instanceof AssociationObjectRDFNode) {
            AssociationObjectRDFNode associationObjectRdfNode = (AssociationObjectRDFNode) obj;
            return rdfNode.equals(associationObjectRdfNode.rdfNode);
        } else if (obj instanceof RDFNode) {
            RDFNode node = (RDFNode)obj;
            return this.rdfNode.equals(node);
        } else {
            return false;
        }
    }

    public boolean eqaulsType(Object obj) {
        if(obj instanceof AssociationObjectRDFNode) {
            AssociationObjectRDFNode associationObjectRdfNode = (AssociationObjectRDFNode) obj;
            return rdfType.equals(associationObjectRdfNode.rdfType);
        } else if (obj instanceof RDFNode) {
            RDFNode type = (RDFNode)obj;
            return this.rdfType.equals(type);
        } else {
            return false;
        }
    }

    public boolean equalsNode(Object obj) {
        if(obj instanceof AssociationObjectRDFNode) {
            AssociationObjectRDFNode associationObjectRdfNode = (AssociationObjectRDFNode) obj;
            return rdfNode.equals(associationObjectRdfNode.rdfNode);
        } else if (obj instanceof RDFNode) {
            RDFNode node = (RDFNode)obj;
            return this.rdfNode.equals(node);
        } else {
            return false;
        }
    }

    public boolean equalsTypeAndValue(Object obj) {
        if(obj instanceof  AssociationObjectRDFNode) {
            AssociationObjectRDFNode node = (AssociationObjectRDFNode) obj;
            if(node.rdfType != rdfType) return false;
            if(!node.data.toString().equals(data.toString())) return false;
            return true;
        } else {
            return false;
        }
    }

    public AssociaitonObjecTypeAndValue toTypeAndValue() {
        return new AssociaitonObjecTypeAndValue(this);
    }

    public static boolean containsType(Set<AssociationObjectRDFNode> assSet, RDFNode nodeType) {
        Iterator<AssociationObjectRDFNode> it = assSet.iterator();
        if (nodeType==null) {
            while (it.hasNext())
                if (it.next()==null)
                    return true;
        } else {
            while (it.hasNext())
//                if (node.equals(it.next()))
                if (it.next().eqaulsType(nodeType))
                    return true;
        }
        return false;
    }

    public static boolean containsAllType(Set<AssociationObjectRDFNode> assSet, Set<RDFNode> typeSet) {
        for (RDFNode type : typeSet)
            if(!containsType(assSet, type))
                return false;
        return true;
    }

    public static AssociationObjectRDFNode getAssociationNodeByNode(Set<AssociationObjectRDFNode> associationObjectRDFNodeSet, RDFNode rdfNode) {
        for (AssociationObjectRDFNode assNode : associationObjectRDFNodeSet) {
            if(assNode.rdfNode.equals(rdfNode)) {
                return assNode;
            }
        }
        return null;
    }

    public static AssociationObjectRDFNode getAssociationNodeByType(Set<AssociationObjectRDFNode> associationObjectRDFNodeSet, RDFNode rdfType) {
        for (AssociationObjectRDFNode assNode : associationObjectRDFNodeSet) {
            if(assNode.rdfType.equals(rdfType)) {
                return assNode;
            }
        }
        return null;
    }

    public static Set<RDFNode> getTypeSet(Set<AssociationObjectRDFNode> nodeSet) {
        Set<RDFNode> typeSet = new HashSet<>();
        for (AssociationObjectRDFNode node : nodeSet) {
            typeSet.add(node.rdfType);
        }
        return typeSet;
    }
}
