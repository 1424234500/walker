package com.walker.core.pipe;

import com.walker.core.database.Redis;
import com.walker.core.database.Redis.Fun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * 
 * 使用redis list作为存储结构
 * 
 * 常用于多进程 多线程生产 多线程消费 上下文隔离场景
 * 只被消费一次 抢占处理
 * 
 * 避免上下级影响
 * 并提供缓冲功能
 * 
 */
public class PipeRedisImpl implements Pipe<String>{
	private static Logger log = LoggerFactory.getLogger(PipeRedisImpl.class);

	/**
	 * 簇列
	 */
	private String key;

	/**
	 * 线程池消费 每个线程都去消费
	 */
	private ExecutorService threadPool;
	
	@Override
	public void start(String key){
		this.key = key;
		
		Boolean res = Redis.getInstance().doJedis(new Fun<Boolean>() {
			@Override
			public Boolean make(Jedis jedis) {
				return jedis != null;
			}
		});

		log.warn("Start res " + res);
		if(!res)
			throw new PipeException("start error");
	}

	@Override
	public void stop(){
		log.info("stop");	
		Redis.getInstance().doJedis(new Fun<Long>() {
			@Override
			public Long make(Jedis jedis) {
				return jedis.del(key);
			}
		}) ;
	}

	@Override
	public boolean remove(String obj) {
		return Redis.getInstance().doJedis(new Fun<Long>() {
			@Override
			public Long make(Jedis jedis) {
				return jedis.del(key);
			}
		}) > 0;
	}

	@Override
	public String get() {
		return Redis.getInstance().doJedis(new Fun<String>() {
			@Override
			public String make(Jedis jedis) {
				return jedis.lpop(key);
			}
		});
	}

	@Override
	public boolean put(Collection<String> objs) {
		return Redis.getInstance().doJedis(new Fun<Long>() {
			@Override
			public Long make(Jedis jedis) {
				return jedis.rpush(key, objs.toArray(new String[0]));
			}
		}) > objs.size();
	}

	@Override
	public boolean put(String obj) {
		return Redis.getInstance().doJedis(new Fun<Long>() {
			@Override
			public Long make(Jedis jedis) {
				return jedis.rpush(key, obj);
			}
		}) > 0;
	}

	@Override
	public boolean putHead(Collection<String> objs) {
		return Redis.getInstance().doJedis(new Fun<Long>() {
			@Override
			public Long make(Jedis jedis) {
				return jedis.lpush(key, objs.toArray(new String[0]));
			}
		}) >= objs.size();
	}

	@Override
	public boolean putHead(String obj) {
		return Redis.getInstance().doJedis(new Fun<Long>() {
			@Override
			public Long make(Jedis jedis) {
				return jedis.lpush(key, (obj));
			}
		}) > 0;
	}

	@Override
	public long size() {
		return Redis.getInstance().doJedis(new Fun<Long>() {
			@Override
			public Long make(Jedis jedis) {
				return jedis.llen(key);
			}
		}) ;
	}

	/**
	 * 单线程 定时轮询  拿到资源 存入队列 新建线程处理     多拿存下来慢慢用
	 * 多线程 各自轮询  拿到资源 消费处理 继续拿资源	  量力获取
	 */
	@Override
	public void startConsumer(int threadSize, final com.walker.core.aop.Fun<String> executer){
		log.warn("StartConsumer");
		if(threadSize <= 0)return;
		threadPool = Executors.newFixedThreadPool(threadSize);
		for(int i = 0; i < threadSize; i++) {
			final int now = i;
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					String keyJedis = key + "-" + now;
					log.warn("Start thread " + now);
					while(! Thread.interrupted()) {
						//！！！！！！消费 加锁 互斥问题 lpop
						String obj = Redis.getInstance().doJedis(new Fun<String>(){
							@Override
							public String make(Jedis jedis) {
								return jedis.lpop(key);//get();;
							}
						});
						if(obj != null) {
							log.debug("Comsumer get " + obj.toString());
							executer.make(obj);
						}else {
							try {
								Thread.sleep(Pipe.SLEEP_THREAD);
							} catch (InterruptedException e) {
								log.error(e.getMessage(), e);
							}
						}
					}

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
				e.printStackTrace();
			}
		}
	}

	
	
}
