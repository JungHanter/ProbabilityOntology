package ssu.sel.po.utils;

import org.apache.jena.rdf.model.RDFNode;

import java.util.*;

/**
 * Created by hanter on 2016. 1. 8..
 */

public class Combination {
    public static Set<Set<RDFNode>> combination(Object[] elements, int K){
        Set<Set<RDFNode>> sets = new HashSet<>();
        int N = elements.length;

        c(N,K);

        int combination[] = new int[K];

        int r = 0;
        int index = 0;

        while(r >= 0) {
            if(index <= (N + (r - K))) {
                combination[r] = index;

                if(r == K-1) {
                    Set<RDFNode> set = getSet(combination, elements);
                    sets.add(set);
                    index++;
                } else {
                    index = combination[r]+1;
                    r++;
                }
            } else {
                r--;
                if (r > 0) index = combination[r]+1;
                else index = combination[0]+1;
            }
        }
        return sets;
    }

    private static int c(int n, int r) {
        int nf=fact(n);
        int rf=fact(r);
        int nrf=fact(n-r);
        int npr=nf/nrf;
        int ncr=npr/rf;

//        System.out.println("C("+n+","+r+") = "+ ncr);
        return ncr;
    }

    private static int fact(int n) {
        if(n == 0) return 1;
        else return n * fact(n-1);
    }

    private static Set<RDFNode> getSet(int[] combination, Object[] elements){
        Set<RDFNode> set = new HashSet<>();
        for(int z = 0 ; z < combination.length; z++){
            set.add((RDFNode)elements[combination[z]]);
        }
        return set;
    }
}
