package com.walker.core.aop;
/**
 * 通用泛型回调接口
 *
 * 参数	泛型定义
 * 返回值	泛型定义
 *
 * @param <A, T>
 */
public interface FunArgsReturn<A, T>{
	T make(A obj) ;
}

