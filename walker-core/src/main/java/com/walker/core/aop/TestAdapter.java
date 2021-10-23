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


public abstract class TestAdapter implements TestModel {
	protected Logger log = LoggerFactory.getLogger("test");

	@Override
	public void test() {
		log.warn(Context.beginTip(getClass()));
		boolean res = false;
		try {
			res = doTest();
		}catch(Exception e) {
			res = false;
			log.error(this.toString() + " " + e.getMessage(), e);
			throw new ErrorException(e);
		}
		if(res) {
			log.warn(Context.okTip(getClass()));
		}else {
			throw new ErrorException(Context.errorTip(getClass()));
		}
		log.warn(Context.endTip(getClass()));		
	}

	/**
	 * 返回false则抛出异常 
	 */
	public boolean doTest() {return true;};
	public boolean doInit() {return true;};
	public boolean doUninit() {return true;};

	@Override
	public void init() {
		log.warn(Context.beginTip(getClass()));
		boolean res = false;
		try {
			res = doInit();
		}catch(Exception e) {
			res = false;
			e.printStackTrace();
			log.error(e.getMessage(), e);
		} 
		if(res) {
			log.warn(Context.okTip(getClass()));
		}else {
			throw new RuntimeException(Context.errorTip(getClass()));
		}
		log.warn(Context.endTip(getClass()));		
	}

	@Override
	public void uninit() {
		log.warn(Context.beginTip(getClass()));
		boolean res = false;
		try {
			res = doUninit();
		}catch(Exception e) {
			res = false;
			e.printStackTrace();
			log.error(e.getMessage(), e);
		} 
		if(res) {
			log.warn(Context.okTip(getClass()));
		}else {
			throw new ErrorException(Context.errorTip(getClass()));
		}
		log.warn(Context.endTip(getClass()));		
	}
}


