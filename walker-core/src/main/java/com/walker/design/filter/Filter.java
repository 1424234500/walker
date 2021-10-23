package com.walker.design.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param <T> 入参
 * @param <R> 过滤链返回结果
 */
interface Filter<T, R> {
    Logger log = LoggerFactory.getLogger(Filter.class);

    default R invoke(FilterChain<T, R> filterChain, T args) {
        return filterChain.invoke(args);
    }

    default String info() {
        return this.toString();
    }

}
