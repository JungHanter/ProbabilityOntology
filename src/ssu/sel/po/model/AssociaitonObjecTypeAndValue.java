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

    @Override
    public int hashCode() {
        return data.getValue().hashCode();
    }

    public static boolean equalsSet(Set<AssociaitonObjecTypeAndValue> set1,
                                    Set<AssociaitonObjecTypeAndValue> set2) {
        if (set1 == set2)
            return true;
        if (set1.size() != set2.size())
            return false;
//        return set1.containsAll(set2);
        return containsAll(set1, set2);
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



    public static boolean contains(Set<AssociaitonObjecTypeAndValue> assSet,
                                   AssociaitonObjecTypeAndValue ass) {
        Iterator<AssociaitonObjecTypeAndValue> it = assSet.iterator();
        if (ass==null) {
            while (it.hasNext())
                if (it.next()==null)
                    return true;
        } else {
            while (it.hasNext())
                if (ass.equals(it.next()))
                    return true;
        }
        return false;
    }

    public static boolean containsAll(Set<AssociaitonObjecTypeAndValue> set1,
                                      Set<AssociaitonObjecTypeAndValue> set2) {
        for (AssociaitonObjecTypeAndValue tv : set2)
            if(!contains(set1, tv))
                return false;
        return true;
    }
}
