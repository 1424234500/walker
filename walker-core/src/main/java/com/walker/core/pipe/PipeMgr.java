package com.walker.core.pipe;

import com.walker.core.aop.TestAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 管道控制器 
 *
 */
public class PipeMgr extends TestAdapter{
	private static Logger log = LoggerFactory.getLogger("pipe");

	private PipeMgr() {}

	public enum Type {
		PIPE,FILE,DATABASE,REDIS,REDIS_BROADCAST
	}
	@SuppressWarnings("unchecked")
	public static <T> Pipe<T> getPipe(Type type, String key){
		Pipe<T> pipe = null;
		switch(type) {
		case REDIS_BROADCAST:
			pipe = (Pipe<T>) new PipeRedisBroadcastImpl();
			break;
		case REDIS:
			pipe = (Pipe<T>) new PipeRedisImpl();
			break;
		case PIPE:
		default:
			pipe = (Pipe<T>) new PipeListImpl<T>();
		}
		pipe.start(key);
		return pipe;
	}
	public boolean doTest() {
		return  PipeMgr.getPipe(Type.PIPE, "test") == null;
	}

}
