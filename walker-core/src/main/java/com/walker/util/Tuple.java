package com.walker.util;


/**
 * 元组 模式 构建 多元素返回
 */
public class Tuple<V1, V2> {
    public final V1 v1;
    public final V2 v2;

    public Tuple(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

} 