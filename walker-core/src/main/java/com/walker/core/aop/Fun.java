package com.walker.core.aop;
/**
 * 通用泛型回调接口
 *
 * 参数	泛型定义
 * 返回值	协变
 *
 * @param <A>
 */
public interface Fun<A>{ 
	void make(A obj) ;
}

