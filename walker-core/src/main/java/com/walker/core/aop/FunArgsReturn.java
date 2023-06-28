package com.walker.core.aop;
/**
 * 通用泛型回调接口
 *
 * 参数	泛型定义
 * 返回值	泛型定义
 *
 */
public interface FunArgsReturn<ARG, RES>{
	RES make(ARG obj);
}

