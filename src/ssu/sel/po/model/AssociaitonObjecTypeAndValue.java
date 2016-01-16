package ssu.sel.po.model;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;

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

    public RDFNode getRdfType() {
        return rdfType;
    }

    public Literal getData() {
        return data;
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
            if(!typeValue.data.toString().equals(data.toString())) return false;
            return true;
        } else {
            return false;
        }
    }
}
