package com.walker.core.pipe;

import com.walker.core.aop.Fun;
import com.walker.core.database.Redis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 使用redis publish subscribe 传递
 * 
 * 常用于多进程 多线程发布 多线程订阅 上下文隔离场景
 * 实现一对多 一人发布 多人复制处理
 * 
 * 避免上下级影响
 * 只提供订阅后的 弱缓冲功能
 * 
 * 
 */
public class PipeRedisBroadcastImpl implements Pipe<String>{
	private static Logger log = LoggerFactory.getLogger(PipeRedisBroadcastImpl.class);

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
		
		Boolean res = Redis.getInstance().doJedis(new Redis.Fun<Boolean>() {
			@Override
			public Boolean make(Jedis jedis) {
				return jedis == null;
			}
		});

		log.warn("Start res " + res);
		if(!res)
			throw new PipeException("start error");
	}
	@Override
	public void stop(){
		log.info("stop");		
	}

	@Override
	public boolean remove(String obj) {
		return false;
	}

	@Override
	public String get() {
		return null;
	}

	@Override
	public boolean put(Collection<String> objs) {
		return Redis.getInstance().doJedis(new Redis.Fun<Long>() {
			@Override
			public Long make(Jedis jedis) {
				long res = 0;
				for(String obj : objs) {
					res += jedis.publish(key, obj);
				}
				return res;
			}
		}) >= objs.size();
	}

	@Override
	public boolean put(String obj) {
		return Redis.getInstance().doJedis(new Redis.Fun<Long>() {
			@Override
			public Long make(Jedis jedis) {
					return jedis.publish(key, obj);
			}
		}) >= 0;
	}

	@Override
	public boolean putHead(Collection<String> objs) {
		return true;
	}

	@Override
	public boolean putHead(String obj) {
		return true;
	}

	@Override
	public long size() {
		return -1;
	}

	/**
	 * 1线程 定时轮询  拿到资源 新建线程处理
	 * 多线程 各自轮询 消费 消费完后继续拿资源
	 */
	@Override
	public void startConsumer(int threadSize, final Fun<String> executer) {
		log.warn("StartConsumer");
		if(threadSize <= 0)return;

		threadPool = Executors.newFixedThreadPool(threadSize);
		Redis.getInstance().doJedis(new Redis.Fun<Object>() {
            @Override
            public Object make(Jedis jedis) {
                jedis.subscribe(new JedisPubSub() {
                    public void onMessage(String channel, final String message) {
                        log.debug("Consumer subcribe [" + channel + "] " + message);
                        threadPool.execute(new Runnable() {
                            public void run() {
                                executer.make(message);
                            }
                        });
                    }

                    public void onSubscribe(String channel, int subscribedChannels) {
                        log.debug("onSubscribe channel:" + channel + " subscribedChannels:" + subscribedChannels);
                    }

                    public void onUnsubscribe(String channel, int subscribedChannels) {
                        log.debug("onUnsubscribe channel:" + channel + " subscribedChannels:" + subscribedChannels);
                    }
                }, key);
                return null;
            }
        });

	}
	@Override
	public void stopConsumer() {
		if(threadPool != null && !threadPool.isShutdown()) {
			threadPool.shutdown();


            Redis.getInstance().doJedis(new Redis.Fun<Object>() {
                @Override
                public Object make(Jedis jedis) {


                    return null;
                }
            });

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
