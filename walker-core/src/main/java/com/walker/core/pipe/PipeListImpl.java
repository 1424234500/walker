package com.walker.core.pipe;

import com.walker.core.aop.Fun;
import com.walker.core.pipe.PipeMgr.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * LinkedBlockingQueue实现 
 * 
 * 常用于单java程序单进程 多线程生产 多线程消费 上下文隔离场景
 * 一对一 只被消费一次 抢占处理
 * 
 * 避免上下级影响
 * 并提供缓冲功能
 * 
 */
public class PipeListImpl<T> extends PipeAdapter<T>{
	private static Logger log = LoggerFactory.getLogger(PipeListImpl.class);

	private LinkedBlockingDeque<T> list;
	
	/**
	 * 线程池消费 每个线程都去消费
	 */
	private ExecutorService threadPool;
	
	@Override
	public void start(String key) {
		list = new LinkedBlockingDeque<T>();
	}

	@Override
	public void stop() {
		log.info("stop");		
		list.clear();
	}

	@Override
	public boolean remove(T obj) {
		return list.remove(obj);
	}

	@Override
	public T get() {
		return list.poll();
	}

	@Override
	public boolean put(Collection<T> objs) {
		boolean res = list.addAll(objs);
		afterAdd();
		return res;

	}

	@Override
	public boolean put(T obj) {
		list.add(obj);
		afterAdd();
		return true;
	}

	@Override
	public boolean putHead(Collection<T> objs) {
		for(T t : objs) {
			list.push(t);
		}
		afterAdd();
		return true;
	}

	@Override
	public boolean putHead(T obj) {
		list.push(obj);
		afterAdd();
		return true;
	}
	private void afterAdd(){
		if(threadPool != null){
			threadPool.notifyAll();
		}
	}

	@Override
	public long size() {
		return list.size();
	}

	public static void main(String argv[]) {
		Pipe<String> pipe = PipeMgr.getPipe(Type.PIPE, "test");
		pipe.startConsumer(10, new Fun<String>() {
			@Override
			public void make(String obj) {
				try {
					Thread.sleep((long) (Math.random() * 1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		});
		
		for(int i = 0; i < 40; i++) {
			pipe.put("s" + i);
		}
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < 10; i++) {
			pipe.putHead("00000s" + i);
		}
		
		pipe.stop();
		log.error("aaaaaaaaaaa");
	}

	
	
	
	
	
}
