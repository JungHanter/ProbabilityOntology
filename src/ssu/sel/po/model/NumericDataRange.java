package ssu.sel.po.model;

import org.apache.jena.rdf.model.RDFNode;
import ssu.sel.po.utils.LiteralValueType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hanter on 2016. 1. 11..
 */
public class NumericDataRange<T extends Number> extends DataRange<T> {
    public NumericDataRange(LiteralValueType valueType) {
        super(valueType);
    }

    public boolean hasValueRange(T value) {
        T min = getMin();
        T max = getMax();

        if(value.doubleValue() < min.doubleValue() ||
                value.doubleValue() > max.doubleValue()) return false;
        else return true;
    }

    public void addValue(T value) {
        if(valueRangeList.size() == 0) {
            valueRangeList.add(0, value);
            valueRangeList.add(1, value);
        } else {
            if(!hasValueRange(value)) {
                T min = getMin();
                T max = getMax();

                if(value.doubleValue() < min.doubleValue()) {
                    valueRangeList.remove(0);
                    valueRangeList.add(0, value);
                } else if(value.doubleValue() > max.doubleValue()) {
                    valueRangeList.remove(1);
                    valueRangeList.add(1, value);
                }
            }
        }
    }

    public T getMin() {
        return valueRangeList.get(0);
    }

    public T getMax() {
        return valueRangeList.get(1);
    }
}
