package com.walker.design.iterator;

import java.util.Iterator;

/**
 * 午餐 arr结构
 *
 * @param <T>
 */
public class MenuLaunch<T> implements Iterator<T> {
    T[] items;
    Integer point = 0;

    public MenuLaunch() {
    }

    @Override
    public boolean hasNext() {
        return point < items.length;
    }

    @Override
    public T next() {
        point++;
        return items[point - 1];
    }

    public Iterator createIterator() {
        return this;
    }


    public T[] getItems() {
        return items;
    }

    public MenuLaunch setItems(T... items) {
        this.items = items;
        return this;
    }
}
