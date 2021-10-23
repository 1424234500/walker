package com.walker.core.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 缓存
 * db
 * 锁
 *
 *
 */
public interface FunCacheDb<K, V> {
	/**
	 * 默认 锁住查询db单key的不释放时间
	 */
	int DEFAULT_LOCK_DB_KEY_TIME = 60;
	Logger log = LoggerFactory.getLogger(FunCacheDb.class);
	String KEY_LOCK = "lock:make:";


	String KEY_LOCK_GET_CACHE_OR_DB = "getCacheOrDb:";
	String KEY_GET_CACHE_OR_DB = "cache:getCacheOrDb:";

	String KEY_LOCK_INIT_CACHE_OR_DB =  "initCacheOrDb:";
	String KEY_INIT_CACHE_OR_DB_OK =  "cache:initCacheOrDb:isok:";


	/**
	 * 缓存是否可用
	 */
	AtomicBoolean CACHE_IS_OK = new AtomicBoolean(true);

	/**
	 * 当从数据库获取后 如何设置到缓存
	 *
	 * 异常认为缓存异常
	 * 返回false认为设置缓存失败 但缓存正常
	 *
	 * @param key
	 */
	boolean setCache(K key, V valueFromDb, int expireSec);

	/**
	 * 如何从缓存获取值
	 *
	 * 计数
	 * 单位时间 缓存读取总次数	缓存读取异常次数
	 *
	 * 如果缓存获取异常  缓存宕机 如何计数阈值
	 *
	 * 熔断限流
	  *
	 * 异常认为缓存异常
	 *
	 * @param key
	 */
	V getCache(K key);

	/**
	 * 当缓存没获取到值时 加锁 避免 穿透  如何从数据库获取值
	 *
	 * 异常认为db异常
	 *
	 * @param key
	 */
	V getDb(K key);
	/**
	 * 设置到数据库 清空缓存 延迟再次清理
	 *
	 * 异常认为db异常
	 * 返回false认为db正常 但数据操作异常
	 *
	 * @param key
	 */
	boolean setDb(K key);
	/**
	 * 如何加锁 单机锁  集群锁
	 *
	 * 异常 或者 返回空串 认为加锁失败
	 *
	 * @param lockName
	 * @param secondsToExpire
	 * @param secondsToWait
	 */
	String tryLock(String lockName, int secondsToExpire, int secondsToWait);

	/**
	 * 如何对应的释放锁
	 *
	 * 异常 或者 返回false 认为解锁失败
	 *
	 * @param lockName
	 * @param identifier
	 */
	boolean releaseLock(String lockName, String identifier);


}

