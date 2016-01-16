package ssu.sel.po.model;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by hanter on 2016. 1. 16..
 */
public class AssociaitonObjecTypeAndValue {
    final RDFNode rdfType;
    final Literal data;

    public AssociaitonObjecTypeAndValue(RDFNode rdfType, Literal data) {
        this.rdfType = rdfType;
        this.data = data;
    }

    public AssociaitonObjecTypeAndValue(AssociationObjectRDFNode node) {
        this.rdfType = node.getRdfType();
        this.data = node.getDataRDF();
    }

    public RDFNode getRdfType() {
        return rdfType;
    }

    public Literal getData() {
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
        return rdfType.toString() + " " + data.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  AssociaitonObjecTypeAndValue) {
            AssociaitonObjecTypeAndValue typeValue = (AssociaitonObjecTypeAndValue) obj;
            if(typeValue.rdfType != rdfType) return false;
//            if(!typeValue.data.toString().equals(data.toString())) return false;
            if(!typeValue.data.getValue().equals(data.getValue())) return false;
            return true;
        } else if (obj instanceof AssociationObjectRDFNode) {
            AssociationObjectRDFNode node = (AssociationObjectRDFNode) obj;
            if(node.rdfType != rdfType) return false;
            if(!node.data.toString().equals(data.toString())) return false;
            return true;
        } else {
            return false;
        }
    }

    public static boolean equalsSet(Set<AssociaitonObjecTypeAndValue> set1,
                                    Set<AssociaitonObjecTypeAndValue> set2) {
        if (set1 == set2)
            return true;
        if (set1.size() != set2.size())
            return false;
        return set1.containsAll(set2);
    }

    public static Set<RDFNode> getTypeSet(Set<AssociaitonObjecTypeAndValue> typeValSet) {
        Set<RDFNode> typeSet = new HashSet<>();
        for (AssociaitonObjecTypeAndValue typeVal : typeValSet) {
            typeSet.add(typeVal.rdfType);
        }
        return typeSet;
    }

    public static boolean containsSet(Set<Set<AssociaitonObjecTypeAndValue>> typeValueSets,
                                      Set<AssociaitonObjecTypeAndValue> typeValueSet) {
        Iterator<Set<AssociaitonObjecTypeAndValue>> it = typeValueSets.iterator();
        if (typeValueSet==null) {
            while (it.hasNext())
                if (it.next()==null)
                    return true;
        } else {
            while (it.hasNext())
                if (equalsSet(it.next(), typeValueSet))
                    return true;
        }
        return false;
    }


    public static boolean contains(Set<AssociationObjectRDFNode> assSet, RDFNode nodeType) {
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

    public static boolean containsAll(Set<AssociationObjectRDFNode> assSet, Set<RDFNode> typeSet) {
        for (RDFNode type : typeSet)
            if(!contains(assSet, type))
                return false;
        return true;
    }
}
