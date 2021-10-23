package com.walker.core.aop;
/**
 * 通用泛型回调接口
 *
 * 参数	泛型定义
 * 返回值	无
 *
 * @param <A>
 */
public interface FunArgs<A>{
	void make(A obj) ;
}
