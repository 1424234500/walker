package com.walker.core.aop;
/**
 *
 * 用于需要启动测试的模块 
 * 抛出runtime异常   TipException
 * 
 * eg：
 * 		缓存启动 自检
 * 		redis连接启动 自检
 * 
 */

public interface Connector {
	/**
	 * 装载 初始化
	 */
	void init();
	
	/**
	 * 自检
	 */
	void check();
	
	/**
	 * 卸载
	 */
	void uninit();
	
}


