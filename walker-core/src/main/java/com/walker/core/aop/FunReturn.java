package com.walker.core.aop;
/**
 * 通用泛型回调接口
 *
 * 参数	无
 * 返回值	泛型
 *
 * @param <T>
 */
public interface FunReturn<T>{
	<T> T make() ;
}

