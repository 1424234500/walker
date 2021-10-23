package com.walker.design.adapter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Test {
    private static final Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] argv) {

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        Iterator<Integer> iterator = new AdapterListIterator<>(list);
        while (iterator.hasNext()) {
            Integer i = iterator.next();
            log.info("N:" + i);
        }

    }


}



