package com.walker.util;

import java.util.ArrayList;
import java.util.List;

/**
 * List工厂类 new ListBuilder<T>().add(object).add(object).build();
 *
 * @author Walker
 * 2017年12月11日 09点42分
 */
public class ListBuilder<T> {
    List<T> list;

    public ListBuilder() {
        list = new ArrayList<>();
    }

    public ListBuilder add(T obj) {
        list.add(obj);
        return this;
    }

    public List<T> build() {
        return list;
    }

}