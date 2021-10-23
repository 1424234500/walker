package com.walker.core.aop;
/**
 * 测试接口
 * 用于需要启动测试的模块 
 * 抛出runtime异常   TipException
 * 
 * eg：
 * 		缓存启动 自检
 * 		redis连接启动 自检
 * 
 */

public interface TestModel {
	/**
	 * 初始化
	 */
	public void init();
	
	/**
	 * 测试是否正常
	 */
	public void test(); 
	
	/**
	 * 反初始化
	 */
	public void uninit();
	
}


