package ssu.sel.po.model;

import org.apache.jena.rdf.model.RDFNode;
import ssu.sel.po.utils.LiteralValueType;

import java.util.*;

/**
 * Created by hanter on 2016. 1. 11..
 */
public class DataRange<T> {
    final protected LiteralValueType valueType;
    final protected List<T> valueRangeList;

    public DataRange(LiteralValueType valueType) {
        this.valueType = valueType;
        this.valueRangeList = new ArrayList<T>();
    }

    public boolean hasValueRange(T value) {
        return valueRangeList.contains(value);
    }

    public void addValue(T value) {
        if(!hasValueRange(value)) {
            valueRangeList.add(value);
        }
    }

    public void getValueRange(int index) {
        valueRangeList.get(index);
    }

    public List<T> getValueRangeList() {
        return valueRangeList;
    }

    public LiteralValueType getValueType() {
        return valueType;
    }
}
