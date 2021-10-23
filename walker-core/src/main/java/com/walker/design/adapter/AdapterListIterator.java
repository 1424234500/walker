package com.walker.design.adapter;

import java.util.Iterator;
import java.util.List;

/**
 * 适配器模式   将一个接口转成另一个接口
 * <p>
 * 日本插头和中国插头不一致，电压不一致，需要适配器转换
 * <p>
 * <p>
 * 让list支持迭代器
 */
public class AdapterListIterator<T> implements Iterator<T> {
    List<T> enumeration;
    volatile Integer point = 0;

    public AdapterListIterator(List<T> enumeration) {
        this.enumeration = enumeration;
    }

    @Override
    public boolean hasNext() {
        return point < enumeration.size();
    }

    @Override
    public T next() {
        point++;
        return enumeration.get(point - 1);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
