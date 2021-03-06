package com.walker.core.database;

import com.walker.common.util.*;
import com.walker.core.aop.FunArgsReturn;
import com.walker.core.exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.params.SetParams;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * jedis设置存取 抽离
 * @author walker
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public  class RedisUtil   {
	private static final String KEY_LOCK = "lock:make:";


	private static final String KEY_LOCK_GET_CACHE_OR_DB = "lock:get:cache:or:db:";
	private static final String KEY_GET_CACHE_OR_DB = "cache:get:cache:or:db:";

	private static final String KEY_LOCK_INIT_CACHE_OR_DB =  "lock:init:cache:or:db:";
	private static final String KEY_INIT_CACHE_OR_DB_OK =  "cache:init:cache:or:db:isok:";


	private static Logger log = LoggerFactory.getLogger(RedisUtil.class);


	/**
	 * 获取key的值 map
	 *
	 * KEY, TYPE, TTL, LEN, VALUE, EXISTS
	 *
	 * @param jedis
	 * @param key
	 * @return
	 */
	public static Bean getKeyInfo(Jedis jedis, String key){
		Bean res = new Bean();
		res.put("KEY", key);
		if(jedis.exists(key)) {
			res.put("EXISTS", true);

			String type = jedis.type(key);
			res.put("TYPE", type);
			res.put("TTL", jedis.ttl(key));
			Long len = -1L;
			Object value = null;
			if (type.equals("string")) {
				value = jedis.get(key);
				len = jedis.strlen(key);
			} else if (type.equals("list")) {
				len = jedis.llen(key);
				value = jedis.lrange(key, 0, len < 50 ? -1 : 50);
			} else if (type.equals("hash")) {
				value = jedis.hgetAll(key);
				len = jedis.hlen(key);
			} else if (type.equals("set")) {
				value = jedis.smembers(key);
				len = jedis.scard(key);
			} else if (type.equals("zset")) {
				value = jedis.zrange(key, 0, len < 50 ? -1 : 50);
				len = jedis.zcard(key);
			} else {
				value = "none";
				res.put("VALUE", "none type");
			}
			res.put("VALUE", value);
			res.put("LEN", len);

		}else {
			res.put("EXISTS", false);
		}
		return res;
	}


	/**
	 * Redis数据结构 ---- 数据库结构[ id:01, name: walker, age: 18 ]
	 * set get key - value
	 * 1.	id:01 - {name:walker, age:18}
	 * 2.	id:01:name - walker
	 * 2.	id:01:age  - 18
	 * 3.	id:01 -  map{
	 * 						id:01,
	 * 						name:walker,
	 * 						age:18
	 * 					} 
	 */
	
	
	/**
	 * 处理list采用rpush结构 否则 全使用序列化 string byte[]
	 * @param jedis
	 * @param key
	 * @return
	 */
	public static <V> V get(Jedis jedis, String key){
		return get(jedis, key, null);
	}
	/**
	 * @param jedis
	 * @param key
	 * @param value
	 * @param secondsExpire
	 */
	public static <V> void set(Jedis jedis, String key, V value, int secondsExpire) {
		jedis.set(key.getBytes(), SerializeUtil.serialize(value));
		if(secondsExpire > 0) {
			//后置设定过期时间
			jedis.expire(key, secondsExpire);
		}
	}


	/*
	首先，先说一下。老外提出了一个缓存更新套路，名为《Cache-Aside pattern》。其中就指出
　　失效：应用程序先从cache取数据，没有得到，则从数据库中取数据，成功后，放到缓存中。
　　命中：应用程序从cache中取数据，取到后返回。
　　更新：先把数据存到数据库中，成功后，再让缓存失效。
	*/
	/**
	 * 缓存获取，穿透实现
	 * 分布式锁 粒度小 避免大量同key数据库访问
	 * 如何解决缓存一致性问题？
	 * @param key0	获取缓存分区
	 * @param key1	缓存map键
	 * @param secondsToExpire	当查询db成功时 设置缓存过期时间
	 * @param secondsToWait	当查询db时锁等待时间 避免穿透
	 * @param getFromDb	穿透查询数据库实现
	 * @return
	 */
	public static <T> T getCacheOrDb(String key0, String key1, int secondsToExpire, int secondsToWait, FunArgsReturn<String, T> getFromDb){
		final String key = KEY_GET_CACHE_OR_DB + key0;
		final String lockName = KEY_LOCK_GET_CACHE_OR_DB + key0 + "::" + key1;

		return Redis.doJedis(new Redis.Fun<T>(){
			@Override
			public T make(Jedis jedis) {
				T res = getMap(jedis, key, key1, null);
				if(res == null){
//					加锁 避免缓存击穿 锁粒度最小程度 key
					String lock = tryLock(jedis, lockName, secondsToExpire, secondsToWait);
					if (lock.length() > 0) {
						try {
							res = getMap(jedis, key, key1, null);  //再次获取缓存
							if (res == null) {
								res = getFromDb.make(key1);
								if(res == null){	//避免缓存穿透  null是否缓存 快速过期来保护数据库  本来计划10分钟过期 则 null1分钟过期 最小5秒过期
//									布隆过滤器预热 性能实现 全局map 精确映射数据库有没有
									setMap(jedis, key, key1, res, Math.max(secondsToExpire/10, 5000));
								}else{
									setMap(jedis, key, key1, res, secondsToExpire);
								}
								log.debug("get from db " + key + " " + key1 + " res is null ? " + (res == null) );
							}
						}finally {
							releaseLock(jedis, lockName, lock);
						}
					}else{
						log.debug("cache funArgsReturn lock no: " + key + " " + key1);
						throw new ErrorException(" no get lock and wait timeout for db date");
					}
				}else{
					log.debug("get from cache " + key + " " + key1);
				}
				return res;
			}
		});
	}

	/**
	 * 初始化 项目启动 读取配置表预热到缓存
	 * 多台服务器同时启动
	 * 一台初始化，其他等待初始化
	 * 如何标识初始化成功 一段时间禁止再初始化 过期的成功标志
	 * @param key0					分区配置
	 * @param secondsToExpire	配置map key过期时间
	 * @param secondsToWait	锁竞争等待时间
	 * @param secondsInitDeta				初始化该分区间隔时间
	 * @param getFromDbList			具体获取数据
	 * @return
	 */
	public static Long initCacheFromDb(String key0, int secondsToExpire, int secondsToWait, int secondsInitDeta, FunArgsReturn<String, Map<String, Object>> getFromDbList){
		final String key = KEY_GET_CACHE_OR_DB + key0;
		final String keyIsOk = KEY_INIT_CACHE_OR_DB_OK + key0;
		final String lockName = KEY_LOCK_INIT_CACHE_OR_DB + key0;

		return Redis.doJedis(new Redis.Fun<Long>(){
			@Override
			public Long make(Jedis jedis) {
				Long res = 0L;

				if(exists(jedis, keyIsOk)){	//加载过的标记还在 表示已经加载完成了
					res = jedis.hlen(key);
				}else{	//未成功 加锁等待 执行
//					加锁 避免缓存击穿 锁粒度最小程度 key
					String lock = tryLock(jedis, lockName, secondsToExpire, secondsToWait);
					if (lock.length() > 0) {
						try {
							if(exists(jedis, keyIsOk)){	//加载过的标记是否还在 若有则标识等待锁期间已经加载完成了 不论成功失败
								res = jedis.hlen(key);
							}else{
								Map<String, Object> keyValues = getFromDbList.make(key0);
								if(keyValues == null || keyValues.size() == 0){	//避免缓存穿透  null是否缓存 快速过期来保护数据库  本来计划10分钟过期 则 null1分钟过期 最小5秒过期
//									布隆过滤器预热 性能实现 全局map 精确映射数据库有没有
									log.debug("get from db " + key + " res is null ? " + (keyValues == null || keyValues.size() == 0) );
//									setMap(jedis, key, key1, res, Math.max(secondsToExpire/10, 5000));
								}else{
									setMap(jedis, key, keyValues, secondsToExpire);
								}
								set(jedis, keyIsOk, lock, secondsInitDeta);	//加载过的标记
 							}
						}finally {
							releaseLock(jedis, lockName, lock);
						}
					}else{
						throw new ErrorException(" no get lock and wait timeout for db date init " + lockName);
					}
				}
				return res;
			}
		}) ;
	}


	/**
	 * 先更新数据库，再删除缓存
	 * 	　　先删除缓存，再更新数据库
	 * 			功能问题：请求A和请求B进行操作 A删缓存 B查出又设置了旧缓存 导致脏旧
	 * 				解决方案：延迟双删， 更新数据库之后，删除缓存，延迟一段时间再次删除缓存（若失败? 重试几次报警记录日志=人工介入？）
	 * 					mysql主从读写分离：未主从同步时 依然有问题 延时间隔
	 *
	 * @param key0	缓存分区
	 * @param key1	缓存map的键
	 * @param secondsToWait	延时等待 数据库主从同步时间 或 并发时间
	 * @param setToDb	穿透持久化实现
	 * @return
	 */
	public static Integer setDbAndClearCache(String key0, String key1, long secondsToWait, FunArgsReturn<String, Integer> setToDb){
		final String key = KEY_GET_CACHE_OR_DB + key0;
		Integer res = 0;
		if(setToDb != null){
			res = setToDb.make(key);	//存入数据库后，等待主从同步到从查询后再次删除，等待避免多线程查询了旧的存入了缓存 脏旧
			if(res > 0) {
				log.debug("setToDb ok size " + res);
				try {
					Redis.doJedis(new Redis.Fun<String>() {
						@Override
						public String make(Jedis jedis) {
							log.debug(" 1 del cache key:" + key + " " + key1 + "size:" + setMap(jedis, key, key1,null, 0));
							return "";
						}
					});
				} catch (Exception e) {
					log.error(key + " " + key1 + " " + e.getMessage(), e);
				}
//				延迟执行 延迟队列？
				ThreadUtil.schedule(new Runnable() {
					@Override
					public void run() {
						try {
							Redis.doJedis(new Redis.Fun<String>() {
								@Override
								public String make(Jedis jedis) {
									log.debug(" 2 del cache key:" + key + " " + key1 + "size:" + setMap(jedis, key, key1,null, 0));
									return "";
								}
							});
						} catch (Exception e) {
							log.error(key + " " + key1 + " " + e.getMessage(), e);
						}
					}
				}, secondsToWait, TimeUnit.MILLISECONDS);
			}else{
				log.warn("setToDb error res num " + res);
			}

		}else{
			log.warn("no set to db ? " + key + " " + key1);
		}
		return res;
	}


	/**
	 * 订阅
	 * @param jedis
	 * @param jedisPubSub
	 * @param channels
	 */
	public static void subscribe(Jedis jedis, JedisPubSub jedisPubSub, String channels) {
		jedis.subscribe(jedisPubSub, channels);
	}
	/**
	 * 发布
	 * @param jedis
	 * @param channel
	 * @param message
	 */
	public static Long publish(Jedis jedis, String channel, String message) {
		return jedis.publish(channel, message);
	}
	

	/**
	 * Redis数据结构 ---- 数据库结构[ id:01, name: walker, age: 18 ]
	 * set get key - value
	 * 1.	id:01 - {name:walker, age:18}
	 * 2.	id:01:name - walker
	 * 2.	id:01:age  - 18
	 * 3.	id:01 -  map{
	 * 						id:01,
	 * 						name:walker,
	 * 						age:18
	 * 					} 
	 */
	
	
	/**
	 * 移除一个指定key 
	 */
	public static long del(Jedis jedis, String key){
		long res = jedis.del(key);
		return res;
	}
	/**
	* 添加一个list 通过byte 序列化 lpush头插入 rpush尾插入
	*/
	public static  long  listRPush(Jedis jedis, String keyName, Collection<? extends String> c){ 
		int res = 0;
		for(String item : c){
			res += jedis.rpush(keyName, item);
		}
		return res;
	}
	/**
	* 添加一个list 通过byte 序列化 lpush头插入 rpush尾插入
	*/
	public static  long listLPush(Jedis jedis, String keyName, Collection<? extends String> c){ 
		int res = 0;
		for(String item : c){
			res += jedis.lpush(keyName, item.toString());
		}
		return res;
	}
	/**
	* 添加 通过byte 序列化 lpush头插入 rpush尾插入
	*/
	public static  long listRpush(Jedis jedis, String keyName, String obj){ 
		return jedis.rpush(keyName, obj);

	}
	/**
	* 添加 通过byte 序列化 lpush头插入 rpush尾插入
	*/
	public static  long listLpush(Jedis jedis, String keyName, String obj){ 
		return jedis.lpush(keyName, obj);
	}
	 
	public static  String lpop(Jedis jedis, String key){

		String res = jedis.lpop(key);

		return res;
	}
	/**
	 * 获取对象 自动解析键值类型
	 */
	public static <V> V get(Jedis jedis, String key, V defaultValue){
		Object res = defaultValue;
//		if(jedis.exists(key)){
//			String type = jedis.type(key);
//			if(type.equals("string")){
//				res = jedis.get(key);
//			}else if(type.equals("list")){
//				res = jedis.lrange(key, 0, -1);
//			}else if(type.equals("hash")){
//				res = (jedis.hgetAll(key));
//			}else if(type.equals("set")){
//				res = (jedis.smembers(key));
//			}else if(type.equals("zset")){
//				res = (jedis.zrange(key, 0, -1));
//			}else{
//				res = jedis.get(key);
//			}
//		}
		byte[]  str = jedis.get(key.getBytes());
		if(str != null && str.length > 0){
			res = SerializeUtil.deserialize(str);
		}
		return (V) res;
	}

	/**
	 * 键值序列化 获取对象 自动解析键值类型
	 */
	public static <V> V getMap(Jedis jedis, String key, String item, V defaultValue){
		Object res = defaultValue;
		byte[]  str = jedis.hget(key.getBytes(), item.getBytes());
		if(str != null && str.length > 0){
			res = LangUtil.turn(str, defaultValue);
		}
		return (V) res;
	}
	/**
	 * 键值序列化 设置对象 自动解析键值类型
	 */
	public static <V> Long setMap(Jedis jedis, String key, String item, V value, int secondsExpire){
		Long res = 0L;
		if(value == null){
			res = jedis.hdel(key, item);
		}else {
			res = jedis.hset(key, item, String.valueOf(value));
		}
		if(secondsExpire > 0) {
			//后置设定过期时间
			jedis.expire(key, secondsExpire);
		}
		return res;
	}

	/**
	 * 键值序列化 批量设置
	 * @param jedis
	 * @param key
	 * @param map
	 * @param secondsExpire
	 */
	public static void setMap(Jedis jedis, String key, Map<String, Object> map, int secondsExpire){
		log.debug("setMap " + key + " " + map + " " + secondsExpire);
		for(String item : map.keySet().toArray(new String[0])) {
			Object value = map.get(item);
			jedis.hset(key, item, String.valueOf(value));
		}
		if (secondsExpire > 0) {
			//后置设定过期时间
			jedis.expire(key, secondsExpire);
		}
	}

	/**
	 * 键值序列化 获取所有
	 * @param jedis
	 * @param key
	 * @return
	 */
	public static Map<String, Object>  getMap(Jedis jedis, String key){
		Map<String, String> map = jedis.hgetAll(key);
		Map<String, Object> res = new LinkedHashMap<>();
		for(String item : map.keySet().toArray(new String[0])) {
			String value = map.get(item);
			res.put(item, String.valueOf(value));
		}
		return res;
	}


	public static  boolean exists(Jedis jedis, String key){
		boolean res = jedis.exists(key);
		return res;
	}
	public static  long size(Jedis jedis, String key) {

		long res = 0;
		if(jedis.type(key).equals("list")) {
			res = jedis.llen(key);
		}else {
			res = jedis.hlen(key);
		}

		return res;
	}
	
	/**
	 * 显示redis所有数据
	 */
	public static  void show(Jedis jedis){
		out("-----------Redis show-----------------");

		//获取所有key 各种类型
		Set<String> set = jedis.keys("*");
		for(String key : set){
			String type = jedis.type(key);
			out("key:" + key + ", type:" + type + "  ");
			if(type.equals("string")){
				out(jedis.get(key));
			}else if(type.equals("list")){
				out(jedis.lrange(key, 0, -1));
			}else if(type.equals("hash")){
				out(jedis.hgetAll(key));
			}else if(type.equals("set")){
				out(jedis.smembers(key));
			}else if(type.equals("zset")){
				out(jedis.zrange(key, 0, -1)); 
			}
			out("#############");
		}

		out("--------------------------------------");
	}
	
	public static  void showHash(Jedis jedis){
		out("-----------Redis showHash-----------------");

		//获取所有key 各种类型
		Set<String> set = jedis.keys("*"); 
		for(String key : set){
			String type = jedis.type(key);
			//out("key:" + key + ", type:" + type + "  "); 
			if(type.equals("hash")){	
				out(key, jedis.hgetAll(key));
			}  
		} 

		out("--------------------------------------");
	}

	public static String tryLock(String lockName, int secondsToExpire, int secondsToWait) {
		return Redis.doJedis(new Redis.Fun<String>() {
			@Override
			public String make(Jedis jedis) {
				return tryLock(jedis, lockName, secondsToExpire, secondsToWait);
			}
		});
	}

	private static final AtomicLong lockNo = new AtomicLong(0);

	/**
	 * 尝试获取分布式锁
	 * @param jedis Redis客户端
	 * @param lockName 锁
	 * @param secondsToExpire	超期时间ms
	 * @param secondsToWait	等待锁时间ms
	 * @return identifier 获取成功具体解锁回执， 失败为空
	 */
	public static String tryLock(Jedis jedis, String lockName, int secondsToExpire, int secondsToWait) {
		String lockKey =  KEY_LOCK + lockName;
		String value = Thread.currentThread().getName() + ":" + LangUtil.getTimeSeqId() + ":" + lockNo; // Thread
		long startTime = System.currentTimeMillis();
		String result = "";
		int cc = 0;
		while(cc++ < 1000) {	//自循环等待 硬限制最多1000次
			try {
				result = jedis.set(lockKey, value, new SetParams().nx().px(secondsToExpire));
//		String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
				if (String.valueOf(result).toUpperCase().contains("OK")) {
					lockNo.addAndGet(1);
					log.debug(Tools.objects2string( "tryLock ok", cc, lockKey, lockName,value, result, secondsToExpire, secondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)), lockNo);
					return value;
				}
			} catch (Exception e) {
				log.error(Tools.objects2string("tryLock exception", cc, lockKey, lockName,value, result, secondsToExpire, secondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)), lockNo, e);
			}
			if(System.currentTimeMillis() > startTime + secondsToWait * 1000){
				log.warn(Tools.objects2string("tryLock error wait timeout", cc, lockKey, lockName,value, result, secondsToExpire, secondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)), "now lock on:" + jedis.get(lockKey) , lockNo);
				break;
			}
			try {//1000ms等待锁，共轮询10次
				TimeUnit.SECONDS.sleep(Math.max(secondsToWait/4, 10));
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
		return "";
	}
	public static Boolean releaseLock(String lockName, String identifier) {
		return Redis.doJedis(new Redis.Fun<Boolean>() {
			@Override
			public Boolean make(Jedis jedis) {
				return releaseLock(jedis, lockName, identifier);
			}
		});
	}
	/**
	 * 释放分布式锁
	 * @param jedis Redis客户端
	 * @param lockName 锁
	 * @param identifier 识别码
	 * @return 是否释放成功
	 */
	public static boolean releaseLock(Jedis jedis, String lockName, String identifier) {
		String lockKey = KEY_LOCK + lockName;

		try {
			String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
			Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(identifier));

			if (! String.valueOf(result).equalsIgnoreCase("0") ) {
				log.debug(Tools.objects2string("release lock ok", lockKey, lockName, identifier));
				return true;
			}else{
				log.error(Tools.objects2string("release lock error have timeout?", lockKey, lockName, identifier, "lockRes:" + result, "but identifier should be " + jedis.get(lockKey)));
			}
		}catch (Exception e){
			log.error(Tools.objects2string("release lock exception", lockKey, lockName, identifier, e.getMessage()), e);
		}
		return false;

	}



	public static  void out(Object...objs){
		Tools.out(objs);
	}
}	
