package com.walker.core.cache;

import com.walker.core.aop.FunArgsReturn;
import com.walker.util.ThreadUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
实现缓存db接口
    依赖 cache, locker

Cache-Aside pattern
失效 db cache
命中 get from cache
更新 db del cache
*/
public abstract class CacheDbAdapter extends CacheLockerBase<String> implements CacheDb<String>{
	private Logger log = LoggerFactory.getLogger(CacheDbAdapter.class);


	/**
	 * 锁前缀
	 * 缓存 key   ${PRE}.${MODULE}.${KEY/LOCK}.${KEY}
	 */
	protected String pre = "cachedb";

	/**
	 * 缓存分区空间
	 */
	protected String module = this.getClass().getSimpleName();

	public CacheDbAdapter setPre(String pre) {
		this.pre = pre;
		return this;
	}
	public CacheDbAdapter setModule(String module) {
		this.module = module;
		return this;
	}

	public String getKeyCache(String key){
		return pre + "." + module + "." + "key" + "." + key;
	}
	private String getKeyLock(String key, String action){
		return pre + "." + module + "." + "lock" + "." + action + "." + key;
	}


	/**
	 * 缓存获取，穿透实现
	 * 分布式锁 粒度小 避免大量同key数据库访问
	 * 如何解决缓存一致性问题？
	 * @param key	缓存map键
	 * @param timemillExpire
	 * @param timemillWait
	 * @param getFromDb
	 */
	@Override
	public <V> V getCacheOrDb(String key, Long timemillExpire, Long timemillWait, FunArgsReturn<String, V> getFromDb) {
		final String keyCache = getKeyCache(key);
		final String keyLock = getKeyLock(key, "get");

		V res;
		if(!containsKey(keyCache)){
//		  加锁 避免缓存击穿 锁粒度最小程度 key
			String lock = tryLock(keyLock, timemillExpire, timemillWait);
			if (lock.length() > 0) {
				try {
					if (!containsKey(keyCache)) {
						res = getFromDb.make(key);
						log.debug("get from db " + key + " " + keyCache + " res is null ? " + (res == null) );
						if(res == null){
							//避免缓存穿透  null是否缓存 快速过期来保护数据库  本来计划10分钟过期 则 null1分钟过期 最小5秒过期
//						  布隆过滤器预热 性能实现 全局map 精确映射数据库有没有
							put(keyCache, res, Math.max(timemillExpire/10, 5000));
						}else{
							put(keyCache, res, timemillExpire);
						}
					}else{
						res = get(keyCache);
						log.debug("get from cache1 " + key + " " + keyCache);
					}
				}finally {
					releaseLock(keyLock, lock);
				}
			}else{
				log.debug("cache funArgsReturn lock no: " + key + " " + keyCache);
				throw new RuntimeException(" no get lock and wait timeout for db date");
			}
		}else{
			res = get(keyCache, (V)null);   //null转换异常????
			log.debug("get from cache " + key + " " + keyCache + " " + res);
		}
		return res;
	}



	/**
	 * 先更新数据库，再删除缓存
	 * 		先删除缓存，再更新数据库
	 * 			功能问题：请求A和请求B进行操作 A删缓存 B查出又设置了旧缓存 导致脏旧
	 * 				解决方案：延迟双删， 更新数据库之后，删除缓存，延迟一段时间再次删除缓存（若失败? 重试几次报警记录日志=人工介入？）
	 * 					mysql主从读写分离：未主从同步时 依然有问题 延时间隔
	 *
	 * @param key	缓存map的键
	 * @param timemillWait	延时等待 数据库主从同步时间 或 并发时间
	 * @param setToDb
	 */
	@Override
	public Long setDbAndClearCache(String key, Long timemillWait, FunArgsReturn<String, Long> setToDb) {
		final String keyCache = getKeyCache(key);
		final String keyLock = getKeyLock(key, "set");
		
		Long res = 0L;
		if(setToDb != null){
			res = setToDb.make(key);	//存入数据库后，等待主从同步到从查询后再次删除，等待避免多线程查询了旧的存入了缓存 脏旧
			if(res > 0) {
				log.debug("setToDb ok size " + res);
				remove(keyCache);
				ThreadUtil.schedule(new Runnable() {
					@Override
					public void run() {
						remove(keyCache);
					}
				}, timemillWait, TimeUnit.MILLISECONDS);
			}else{
				log.warn("setToDb error res num " + res);
			}
		}else{
			log.warn("no set to db ? " + key + " " + keyCache);
		}
		return res;
	}

	/**
	 * 初始化 项目启动 读取配置表预热到缓存
	 * 多台服务器同时启动
	 * 一台初始化，其他等待初始化
	 * 如何标识初始化成功 一段时间禁止再初始化 过期的成功标志
	 * @param timemillExpire	配置map key过期时间
	 * @param timemillWait  等待一段时间初始化
	 * @param getFromDbList
	 */
	@Override
	public <V> Long initCacheFromDb(Long timemillExpire, Long timemillWait, Long timemillInitDeta, FunArgsReturn<String, Map<String, V>> getFromDbList) {
		String key = "init";
		final String keyCache = getKeyCache(key);
        final String keyLock = getKeyLock(key, "init");
        final String keyOk = getKeyLock(key, "init-ok");
		Long res = 0L;

		if(isLocked(keyOk)){ //锁住一段时间 自动释放
			res = size();
		}else{	//未锁住 加锁 锁粒度最小程度 key
			String lock = tryLock(keyLock, timemillExpire, timemillWait);
			if (lock != null && lock.length() > 0) {
				try {
					if(isLocked(keyOk)){
						res = size();
					}else{
						Map<String, V> keyValues = getFromDbList.make(keyCache);
						if(keyValues == null || keyValues.size() == 0){
//避免缓存穿透  null是否缓存 快速过期来保护数据库  本来计划10分钟过期 则 null1分钟过期 最小5秒过期
//布隆过滤器预热 性能实现 全局map 精确映射数据库有没有
							log.debug("get from db " + key + " res is null ? " + (keyValues == null || keyValues.size() == 0) );
						}else{
//							putAll(keyValues);
                            keyValues.forEach((k, obj)->{
                                put(getKeyCache(k), obj);
                                log.info("config file load " + getKeyCache(k) + " " + obj);
                            });
						}
                        tryLock(keyOk, timemillInitDeta, timemillWait);
					}
				}catch (Exception e){   //异常时释放 给其他线程机会 正常等待超时释放 控制初始化间隔
					throw new RuntimeException(" initCacheFromDb error " + keyLock + " " + e.getMessage(), e);
				}finally {
                    releaseLock(keyLock, lock);
				}
			}else{
				throw new RuntimeException(" no get lock and wait timeout for db date init " + keyLock);
			}
		}
		return res;
	}




}
