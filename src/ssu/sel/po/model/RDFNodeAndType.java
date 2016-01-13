package ssu.sel.po.model;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Created by hanter on 2016. 1. 8..
 */
public class RDFNodeAndType {
    final RDFNode rdfNode;
    final RDFNode rdfType;

    public RDFNodeAndType(RDFNode rdfNode, RDFNode rdfType) {
        this.rdfNode = rdfNode;
        this.rdfType = rdfType;
    }

    public RDFNode getRdfNode() {
        return rdfNode;
    }

    public RDFNode getRdfType() {
        return rdfType;
    }

    @Override
    public String toString() {
        return rdfNode.toString() + " " + rdfType.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() != RDFNodeAndType.class) return false;

        RDFNodeAndType rdfNodeAndType = (RDFNodeAndType)obj;
        return rdfNode.equals(rdfNodeAndType.rdfNode);
    }

    public boolean eqaulsType(RDFNodeAndType rdfNodeAndType) {
        return rdfType.equals(rdfNodeAndType.rdfType);
    }
}
