package com.walker.core.util;

import com.walker.core.mode.Bean;
import junit.framework.TestCase;

import java.util.Comparator;

public class SortUtilTest extends TestCase {


    public static void test(int len) {
        Bean[] list = new Bean[len];
        Bean[] res;
        int[] sequence = ArraysUtil.getSequence(len, 0);
        for (int i = 0; i < len; i++) {
            list[i] = (new Bean().put("k", sequence[i]));//Tools.getRandomNum(0, len, 0)));
        }
//		Tools.out(list);
        Comparator<Bean> comp = new Comparator<Bean>() {
            @Override
            public int compare(Bean o1, Bean o2) {
                SortUtil.countIf++;
                return o1.get("k", 0).compareTo(o2.get("k", 0));
            }
        };
        res = SortUtil.bubbleSort(list.clone(), comp);
//		Tools.out(res);
        res = SortUtil.selectionSort(list.clone(), comp);
        res = SortUtil.insertSort(list.clone(), comp);
//		Tools.out(res);
        res = SortUtil.quickSort(list.clone(), comp);
//		Tools.out(res);
        res = SortUtil.timSort(list.clone(), comp);
        Tools.out(res);
    }


}