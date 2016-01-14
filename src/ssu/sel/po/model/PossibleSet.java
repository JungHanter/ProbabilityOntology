package ssu.sel.po.model;

import org.apache.jena.rdf.model.RDFNode;

import java.util.*;

/**
 * Created by hanter on 2016. 1. 9..
 */
public class PossibleSet {
    private final Set<RDFNode> possibleSet;
    private int appearCnt;
    private double support;

//    public PossibleSet(Set<RDFNode> set) {
//        this(set, 1, 0.0);
//    }

    public PossibleSet(Set<RDFNode> set, int appearCnt, double support) {
        possibleSet = set;
        this.appearCnt = appearCnt;
        this.support = support;
    }

    public boolean isTheSet(Set<RDFNode> set) {
        return possibleSet.equals(set);
    }

//    public void addAppearCount() {
//        appearCnt++;
//    }
//
//    public void addAppearCount(int cnt) {
//        appearCnt += cnt;
//    }

    public Set<RDFNode> getPossibleSet() {
        return possibleSet;
    }

    public int getAppearCnt() {
        return appearCnt;
    }

    public double getSupport() {
        return support;
    }

    public int getSize() {
        return possibleSet.size();
    }

}
