package com.walker.core.pipe;

import com.walker.core.aop.Fun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 抽象实现
 * 
 */
public abstract class PipeAdapter<T> implements Pipe<T>{
	private static Logger log = LoggerFactory.getLogger(PipeAdapter.class);


	/**
	 * 线程池消费 每个线程都去消费
	 */
	private ExecutorService threadPool;
	

	@Override
	public void startConsumer(int threadSize, final Fun<T> executer) {
		log.warn("StartConsumer");
		if(threadSize <= 0)return;

		threadPool = Executors.newFixedThreadPool(threadSize);
		for(int i = 0; i < threadSize; i++) {
			final int now = i;
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					log.warn("Start thread " + now);
					while(! Thread.interrupted()) {
						//！！！！！！消费 加锁 互斥问题
						T obj = get();
						if(obj != null) {
							log.debug("Comsumer get " + obj.toString());
							executer.make(obj);
						}else {
							try {
//								log.debug("sleep");
//								Thread.sleep(Pipe.SLEEP_THREAD);
//								参考condition wait notify问题  共享对象可行，redis不行
								wait();	// 等待唤醒机制 主要是sleep方法没有释放锁，而wait方法释放了锁，使得其他线程可以使用同步控制块或者方法。
							} catch (InterruptedException e) {
								log.error(e.getMessage(), e);
							}
						}
					}
					log.warn("Stop thread " + now);
				}
			});
		}		
	}

	@Override
	public void stopConsumer() {
		if(threadPool != null && !threadPool.isShutdown()) {
			threadPool.shutdown();
		}
	}

	@Override
	public void await(long timeout, TimeUnit unit) {
		if(threadPool != null && !threadPool.isShutdown()) {
			try {
				threadPool.awaitTermination(timeout, unit);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

}
