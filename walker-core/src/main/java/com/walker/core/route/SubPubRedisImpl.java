package com.walker.core.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *  异步发布订阅
 *  常用于多进程
 *  一对多 一人发布 订阅者们都处理
 * @param <T>
 * @param <V>
 */
public class SubPubRedisImpl<T, V> implements SubPub<T, V>{
//	private ExecutorService pool;
	private Map<String, Set<OnSubscribe<T, V>>> subscribeTable;
	private static Logger log = LoggerFactory.getLogger(SubPubRedisImpl.class);

	
	@Override
	public List<V> publish(String channel, final T object) {
		List<V> res = new ArrayList<>();
		Set<OnSubscribe<T, V>> list = subscribeTable.get(channel);
		if(list == null) {
			log.warn("No subscriber channel: " + channel);
		}else {
			for(final OnSubscribe<T, V> onSub : list) {
				Res<V> v = onSub.onSubscribe(object);
				res.add(v.res);
				//上下级先后处理对于流程的控制
				if(v.res == SubPub.OnSubscribe.Type.STOP) {
					break;
				}
//				是否异步隔离订阅和发布
//				pool.submit(new Runnable() {
//					public void run() {
//						onSub.onSubscribe(object);
//					}
//				});
			}
		}

		return res;
	}

	@Override
	public Integer subscribe(String channel, OnSubscribe<T, V> onSubscribe) {
		if(onSubscribe == null) {
			log.error("No onSubscribe callback channel: " + channel);
			return -1;
		}
		Set<OnSubscribe<T, V>> list = subscribeTable.get(channel);
		if(list == null) {
			list = new CopyOnWriteArraySet<>();
			subscribeTable.put(channel, list);
		}
		int res = list.size();
		res = list.add(onSubscribe) ? res : -1;
		log.info("subscribe " + onSubscribe.toString() + " " + channel + " " + res);
		return res;
	}

	@Override
	public Integer unSubscribe(String channel, OnSubscribe<T, V> onSubscribe) {
		Set<OnSubscribe<T, V>> list = subscribeTable.get(channel);
		if(list == null) {
			log.error("unsubscribe of null?");
			return -1;
		}
		Boolean res = list.remove(onSubscribe);
		log.info("unSubscribe " + onSubscribe.toString() + " " + channel + " " + res);
		return list.size();
	}
	@Override
	public void init(Integer threadSize) {
//		pool = Executors.newFixedThreadPool(threadSize);	
//        pool = new ThreadPoolExecutor(1, threadSize, 10L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        subscribeTable = new ConcurrentHashMap<>();
	}


}
