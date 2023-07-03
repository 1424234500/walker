package com.walker.core.aop;

import com.walker.core.Context;
import com.walker.core.exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试接口
 * 用于需要启动测试的模块 
 * 抛出runtime异常   TestException
 * 
 * eg：
 * 		缓存启动 自检
 * 		redis连接启动 自检
 * 
 */


public abstract class ConnectorAdapter  {
	protected Logger log = LoggerFactory.getLogger("test");

	public abstract String info();

	/**
	 * 可重复执行 检查连接器状态 是否正常
	 * @return
	 * @throws Exception
	 */
	public Boolean check() throws Exception {
		return true;
	}

	/**
	 * 可重复执行 但应该有状态 不影响单例状态变化
	 * @return
	 * @throws Exception
	 */
	public Boolean init() throws Exception  {
		return true;
	}

	public Boolean uninit() throws Exception  {
		return true;
	}

}


