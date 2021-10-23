package com.walker.design.iterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 早餐 list结构
 *
 * @param <T>
 */
public class MenuBreakfast<T> {
    List<T> items;

    public MenuBreakfast() {
        items = new ArrayList<>();
    }

    public Iterator createIterator() {
        return this.items.iterator();
    }


    public List<T> getItems() {
        return items;
    }

    public MenuBreakfast setItems(T... items) {
        this.items.clear();
        this.items.addAll(Arrays.asList(items));
        return this;
    }
}
