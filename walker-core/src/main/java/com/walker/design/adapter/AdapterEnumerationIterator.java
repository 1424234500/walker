package com.walker.design.adapter;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * 适配器模式
 * 日本插头和中国插头不一致，电压不一致，需要适配器转换
 * <p>
 * <p>
 * 迭代器和旧迭代器转换问题
 */
public class AdapterEnumerationIterator<T> implements Iterator<T> {
    Enumeration<T> enumeration;

    public AdapterEnumerationIterator(Enumeration<T> enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }

    @Override
    public T next() {
        return enumeration.nextElement();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
