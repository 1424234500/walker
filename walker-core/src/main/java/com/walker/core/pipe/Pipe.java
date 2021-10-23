package com.walker.core.pipe;

import com.walker.core.aop.Fun;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 管道 跨 进程通信工具
 * 行字符串-存取  对象存取-编码解码
 * 同步  阻塞 阻塞队列 
 * LinkedBlockingQueue
 * ArrayBlockingQueue
 * 
 * 内存实现
 * redis list
 * redis subscribe
 * 系统管道实现
 * 文件实现
 * 数据库实现
 */
public interface Pipe<T>{
	long TIMEOUT_MS = 10 * 1000;
	int PIPE_SIZE = 1000;
	/**
	 * lock pre
	 */
	String LOCK_PRE = "lock:pipe:";
	/**
	 * 多线程消费 空闲间隔
	 */
	long SLEEP_THREAD = 50;
	/**
	 * 初始化管道
	 * @param key
	 */
	void start(String key);
	/**
	 * 销毁管道
	 * 清空队列
	 */
	void stop();
	/**
	 * 停止管道消费线程池  最多等待时间
	 * 等待执行完毕
	 */
	void await(long timeout, TimeUnit unit);
	/**
	 * 移除元素
	 * @param obj
	 */
	boolean remove(T obj);
	/**
	 * 获取队首 弹出
	 */
	T get();
	
	/**
	 * 存入 排队 
	 * @param objs
	 */
	boolean put(Collection<T> objs);
	/**
	 * 存入 排队
	 * @param obj
	 */
	boolean put(T obj);
	/**
	 * 存入队首 优先
	 * @param objs
	 */
	boolean putHead(Collection<T> objs);
	/**
	 * 存入队首 优先
	 * @param obj
	 */
	boolean putHead(T obj);
	/**
	 * 队列大小
	 */
	long size() ;
	
	
	/**
	 * 开启消费者线程
	 * 线程池
	 * 线程数 执行器
	 */
	void startConsumer(int threadSize, Fun<T> executer) ;

	/**
	 * 关闭消费者线程
	 * 等待线程执行完毕 不再添加新的线程
	 */
	void stopConsumer() ;
	
}


	
