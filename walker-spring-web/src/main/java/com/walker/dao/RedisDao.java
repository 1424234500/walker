package com.walker.dao;

import com.walker.core.aop.FunArgsReturn;
import com.walker.core.aop.FunCacheDb;
import com.walker.core.encode.JsonUtil;
import com.walker.core.exception.ErrorException;
import com.walker.mode.Bean;
import com.walker.mode.Key;
import com.walker.util.*;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.async.RedisScriptingAsyncCommands;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StopWatch;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * 常用redis操作 template实现
 */
@Repository
public class RedisDao {
    /**
     * 默认 锁住查询db单key的不释放时间
     */
    private static final int DEFAULT_LOCK_DB_KEY_TIME = 60;
    private Logger log = LoggerFactory.getLogger(getClass());
    private static final String KEY_LOCK = "lock:make:";


    private static final String KEY_LOCK_GET_CACHE_OR_DB = "lock:getCacheOrDb:";
    private static final String KEY_GET_CACHE_OR_DB = "cache:getCacheOrDb:";

    private static final String KEY_LOCK_INIT_CACHE_OR_DB =  "lock:initCacheOrDb:";
    private static final String KEY_INIT_CACHE_OR_DB_OK =  "cache:initCacheOrDb:isok:";


    @Autowired
    private RedisTemplate redisTemplate;

    final static String UNLOCK_LUA = "" +
            "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
            "then " +
            "    return redis.call(\"del\",KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end ";

    /**
     * 尝试Lock 成功后返回密钥\
     *
     *
     * 1/ 非重入，等待锁时使用线程sleep
     *
     * 2/使用  redis的  SETNX   带过期时间的方法
     *
     * 3/使用ThreadLocal保存锁的值，在锁超时时，防止删除其他线程的锁，使用lua 脚本保证原子性；
     *
     * @param lockName
     * @param secondsToExpire
     */
    public String tryLock(String lockName, int secondsToExpire, int secondsToWait) {

        String lockKey = Key.getLockRedis(lockName);
        String value = Thread.currentThread().getName() + ":" + LangUtil.getTimeSeqId(); // Thread
        byte[] keyByte = lockKey.getBytes(StandardCharsets.UTF_8);
        byte[] valueByte = value.getBytes(StandardCharsets.UTF_8);

        long startTime = System.currentTimeMillis();
        int cc = 0;
        Object result = null;
        while(cc++ < 1000) {	//自循环等待 硬限制最多1000次
            try {
//        		String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
                result = redisTemplate.execute(new RedisCallback() {
                    @Override
                    public Object doInRedis(RedisConnection connection) throws DataAccessException {
                        try{
                            Object nativeConnection = connection.getNativeConnection();
                            String resultString = "";
//                            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
//                            resultString = commands.set(lockKey, value, "NX", "PX", secondsToExpire);
                            if(nativeConnection instanceof RedisAsyncCommands){
                                RedisAsyncCommands command = (RedisAsyncCommands) nativeConnection;
                                resultString = command
                                        .getStatefulConnection()
                                        .sync()
                                        .set(keyByte, valueByte, SetArgs.Builder.nx().ex(secondsToExpire));
                            }else if(nativeConnection instanceof RedisAdvancedClusterAsyncCommands){
                                RedisAdvancedClusterAsyncCommands clusterAsyncCommands = (RedisAdvancedClusterAsyncCommands) nativeConnection;
                                resultString = clusterAsyncCommands
                                        .getStatefulConnection()
                                        .sync()
                                        .set(keyByte, valueByte, SetArgs.Builder.nx().ex(secondsToExpire));
                            }
                            return resultString;
                        }finally {
                            connection.close();
                        }
                    }
                });
                if (String.valueOf(result).toUpperCase().contains("OK")) {
                    log.debug(Tools.objects2string("tryLock ok", cc, lockKey, lockName, value, result, secondsToExpire, secondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)));
                    return value;
                }
            } catch (Exception e) {
                log.error(Tools.objects2string("tryLock exception", cc, lockKey, lockName, value, result, secondsToExpire, secondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)), e);
            }
            if(System.currentTimeMillis() > startTime + secondsToWait * 1000){
                log.warn(Tools.objects2string("tryLock error wait timeout", cc, lockKey, lockName, value, result, secondsToExpire, secondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)) );
                break;
            }
            try {//1000ms等待锁，共轮询10次     cas自旋次数问题控制？
                TimeUnit.MILLISECONDS.sleep(Math.max(secondsToWait * 1000 / 100, (long)(Math.random() * 40 + 10)));
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        return "";
    }

    /**
     * 释放锁
     * 有可能因为持锁之后方法执行时间大于锁的有效期，此时有可能已经被另外一个线程持有锁，所以不能直接删除
     * 使用lua脚本删除redis中匹配value的key
     * @param lockName
     * @param identifier 密钥
     * @return false:   锁已不属于当前线程  或者 锁已超时
     */
    @SuppressWarnings("unchecked")
    public boolean releaseLock(String lockName, String identifier) {
        String lockKey = Key.getLockRedis(lockName);

        byte[] keyBytes = lockKey.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = identifier.getBytes(StandardCharsets.UTF_8);
        Object[] keyParam = new Object[]{keyBytes};

        Object result = redisTemplate.execute(new RedisCallback<Long>() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                try{
                    Object nativeConnection = connection.getNativeConnection();

//                    List<String> keys = new ArrayList<>();
//                    keys.add(lockKey);
//                    List<String> args = new ArrayList<>();
//                    args.add(identifier);
//                    // 集群模式和单机模式虽然执行脚本的方法一样，但是没有共同的接口，所以只能分开执行
//                    // 集群模式
//                    if (nativeConnection instanceof JedisCluster) {
//                        return (Long) ((JedisCluster) nativeConnection).eval(UNLOCK_LUA, keys, args);
//                    }
//
//                    // 单机模式
//                    else if (nativeConnection instanceof Jedis) {
//                        return (Long) ((Jedis) nativeConnection).eval(UNLOCK_LUA, keys, args);
//                    }
//                    return 0L;
//
                    if (nativeConnection instanceof RedisScriptingAsyncCommands) {
                        RedisScriptingAsyncCommands<Object,byte[]> command = (RedisScriptingAsyncCommands<Object,byte[]>) nativeConnection;
                        RedisFuture future = command.eval(UNLOCK_LUA, ScriptOutputType.INTEGER, keyParam, valueBytes);
                        try {
                            return (Long)future.get();
                        } catch (InterruptedException e) {
                            return -1L;
                        } catch (ExecutionException e) {
                            return -2L;
                        }
                    }
                    return 0L;
                }finally {
                    connection.close();
                }
            }
        });
        boolean res = result != null && (Long)result > 0;
        if (res) {
            log.debug(Tools.objects2string("release lock ok", lockKey, lockName, identifier, result));
        }else{
            String v = get(lockKey, "not exists?");
            log.error(Tools.objects2string("release lock error", lockKey, lockName, identifier, result, "value should be", v));
        }
        return res;
    }


    /**
     * 查看是否加锁
     * @param key
     */
    public boolean isLocked(String key) {
        Object o = redisTemplate.opsForValue().get(key);
        return o!=null;
    }

    /**
     * 写入缓存
     * @param key
     * @param value
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            log.error("set " + key + " " + value + " " + e.getMessage(), e);
        }
        return result;
    }
    /**
     * 写入缓存设置时效时间
     * @param key
     * @param value
     */
    public boolean set(final String key, Object value, Integer expireTime) {
        boolean result = false;
        try {
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            redisTemplate.expire(key, expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            log.error("set " + key + " " + value + " " + expireTime + " " + e.getMessage(), e);
        }
        return result;
    }
    /**
     * 写入时效时间 s
     * @param key
     */
    public boolean expire(final String key, int expireSeconds) {
        boolean result = false;
        try {
            redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            log.error("expire " + key + " " + expireSeconds + " " + e.getMessage(), e);
        }
        return result;
    }

    /**
     * 批量删除对应的value
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     * @param pattern
     */
    public void removePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys.size() > 0)
            redisTemplate.delete(keys);
    }
    /**
     * 删除对应的value
     * @param key
     */
    public void remove(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }
    /**
     * 判断缓存中是否有对应的value
     * @param key
     */
    public boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }
    /**
     * 读取缓存
     * @param key
     */
    public Object get(final String key) {
        Object result = null;
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;
    }
    /**
     * 读取缓存
     * @param key
     */
    public <T> T get(final String key, final T defaultValue) {
        T result = null;
        ValueOperations<Serializable, T> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        if(result != null)
            return result;
        else{
            return defaultValue;
        }
    }
    /**
     * 读取缓存
     * @param key
     */
    public <T> T getConfig(final String key, final T defaultValue) {
        T result = null;
        ValueOperations<Serializable, T> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        if(result != null)
            return result;
        else{
            return defaultValue;
        }
    }
    /**
     * 哈希 添加
     * @param key
     * @param hashKey
     * @param value
     */
    public void hmSet(String key, Object hashKey, Object value){
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        hash.put(key,hashKey,value);
    }

    /**
     * 哈希获取数据
     * @param key
     * @param hashKey
     */
    public Object hmGet(String key, Object hashKey){
        HashOperations<String, Object, Object>  hash = redisTemplate.opsForHash();
        return hash.get(key,hashKey);
    }

    /**
     * 列表添加
     * @param k
     * @param v
     */
    public void lPush(String k,Object v){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        list.rightPush(k,v);
    }

    /**
     * 列表获取
     * @param k
     * @param l
     * @param l1
     */
    public List<Object> lRange(String k, long l, long l1){
        ListOperations<String, Object> list = redisTemplate.opsForList();
        return list.range(k,l,l1);
    }

    /**
     * 集合添加
     * @param key
     * @param value
     */
    public void add(String key,Object value){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        set.add(key,value);
    }

    /**
     * 集合获取
     * @param key
     */
    public Set<Object> setMembers(String key){
        SetOperations<String, Object> set = redisTemplate.opsForSet();
        return set.members(key);
    }

    /**
     * 有序集合添加
     * @param key
     * @param value
     * @param scoure
     */
    public <T> void zAdd(String key, T value,double scoure){
        ZSetOperations<String, T> zset = redisTemplate.opsForZSet();
        zset.add(key,value,scoure);
    }
    /**
     * score 不存在就会异常？
     * @param key
     * @param value
     */
    public <T> Double zScore(String key, T value){
        Double res = -1d;
        try {
            ZSetOperations<String, T> zset = redisTemplate.opsForZSet();
            res = zset.score(key, value);
            if(res == null ){
                res = -2d;
            }
        }catch (Exception e){
            res = -3d;
        }
        return res;
    }

    /**
     * 有序集合获取
     * @param key
     * @param scoure
     * @param scoure1
     */
    public Set<Object> rangeByScore(String key,double scoure,double scoure1){
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        return zset.rangeByScore(key, scoure, scoure1);
    }














    /**
     * 键值序列化 获取对象 自动解析键值类型
     */
    public <V> V getMap(String key, String item, V defaultValue){
        HashOperations<String, String, String> hash = redisTemplate.opsForHash();
        String res = hash.get(key, item);
        return LangUtil.turn(res, defaultValue);
    }
    /**
     * 键值序列化 设置对象 自动解析键值类型
     */
    public   <V> Long setMap(String key, String item, V value, int secondesExpire){
        HashOperations<String,String, String> hash = redisTemplate.opsForHash();
        Long res = 0L;
        if(value == null){
            hash.delete(key, item);
        }else {
            hash.put(key, item, String.valueOf(value) );
            res = 1L;
        }
        if(secondesExpire > 0) {
            //后置设定过期时间
            redisTemplate.expire(key, secondesExpire, TimeUnit.SECONDS);
        }
        return res;
    }

    /**
     * 键值序列化 批量设置
     * @param key
     * @param map
     * @param secondesExpire
     */
    public void setMap(String key, Map<String, Object> map, int secondesExpire){
        HashOperations<String,String, String> hash = redisTemplate.opsForHash();

        log.debug("setMap " + key + " " + map + " " + secondesExpire);
        for(String item : map.keySet().toArray(new String[0])) {
            Object value = map.get(item);
            hash.put(key, item, String.valueOf(value));
        }
        if (secondesExpire > 0) {
            //后置设定过期时间
            redisTemplate.expire(key, secondesExpire, TimeUnit.SECONDS);
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
     * @param key0	获取缓存分区 config
     * @param key1	缓存map键  CC_CONFIG_1
     * @param secondsToExpire	当查询db成功时 设置缓存过期时间
     * @param secondsToWait	当查询db时锁等待时间 避免穿透
     * @param getFromDb	穿透查询数据库实现
     */
    public <T> T getCacheOrDb(String key0, String key1, int secondsToExpire, int secondsToWait, FunArgsReturn<String, T> getFromDb){
        final String key = KEY_GET_CACHE_OR_DB + key0;
        final String lockName = KEY_LOCK_GET_CACHE_OR_DB + key0 + ":" + key1;
        StopWatch stopWatch = new StopWatch();

//        熔断
//        如果redis不可用    考虑一段时间熔断    恢复机制
//        如果redis失败率超过50%   考虑一段时间限流    恢复机制
//        这里应该跳过redis并且直接查询数据库  并且通知限流  降级服务
        stopWatch.start("getMap");
        T res = getMap(key, key1, null);
        stopWatch.stop();
        if(res == null){
//					加锁 避免缓存击穿 锁粒度最小程度 key
            stopWatch.start("tryLock");
            String lock = tryLock(lockName, DEFAULT_LOCK_DB_KEY_TIME, secondsToWait);
            stopWatch.stop();
            if (lock.length() > 0) {
                try {
                    stopWatch.start("getMap2");
                    res = getMap(key, key1, null);  //再次获取缓存
                    stopWatch.stop();
                    if (res == null) {
                        stopWatch.start("getDb");
                        res = getFromDb.make(key1);
                        stopWatch.stop();
                        stopWatch.start("setMap");
                        if(res == null){	//避免缓存穿透  null是否缓存 快速过期来保护数据库  本来计划10分钟过期 则 null 1分钟过期 最小5秒过期
//									布隆过滤器预热 性能实现 全局map 精确映射数据库有没有
//                            缓存空值
                            log.warn("get from db " + key + " res is null ? " + (res == null) );
                            setMap(key, key1, "", Math.max(secondsToExpire/100, 5));
                        }else{
                            log.debug("get from db " + key + " res is null ? " + (res == null) + " " + res);
                            setMap(key, key1, res, secondsToExpire);
                        }
                        stopWatch.stop();
                    }
                }finally {
                    stopWatch.start("releaseLock");
                    releaseLock(lockName, lock);
                    stopWatch.stop();
                }
            }else{
                log.debug("cache funArgsReturn lock no: " + key + " " + key1);
                throw new ErrorException(" no get lock and wait timeout for db date");
            }
        }else{
            log.debug("get from cache " + key + " " + key1 + " res " + res);
        }
        log.debug(stopWatch.prettyPrint());
        return res;
    }
    public <T> T getCacheOrDb(String key0, String key1, int secondsToExpire, int secondsToWait, FunCacheDb<String, T> funCacheDb){
        final String key = KEY_GET_CACHE_OR_DB + key0;
        final String lockName = KEY_LOCK_GET_CACHE_OR_DB + key0 + "::" + key1;


//        熔断
//        如果redis不可用    考虑一段时间熔断    恢复机制
//        如果redis失败率超过50%   考虑一段时间限流    恢复机制
//        这里应该跳过redis并且直接查询数据库  并且通知限流  降级服务

        T res = funCacheDb.CACHE_IS_OK.get() ? funCacheDb.getCache(key1) : null;//getMap(key, key1, null);
        if(res == null){
//					加锁 避免缓存击穿 锁粒度最小程度 key
            String lock = funCacheDb.tryLock(lockName, DEFAULT_LOCK_DB_KEY_TIME, secondsToWait);
            if (lock.length() > 0) {
                try {
                    res = getMap(key, key1, null);  //再次获取缓存
                    if (res == null) {
                        res = funCacheDb.getDb(key1);
                        if(res == null){	//避免缓存穿透  null是否缓存 快速过期来保护数据库  本来计划10分钟过期 则 null 1分钟过期 最小5秒过期
//									布隆过滤器预热 性能实现 全局map 精确映射数据库有没有
                            log.warn("get from db " + key + " res is null ? " + (res == null) );
                            funCacheDb.setCache(key1, res, Math.max(secondsToExpire/100, 5));

                        }else{
                            log.debug("get from db " + key + " res is null ? " + (res == null) + " " + res);
                            funCacheDb.setCache(key1, res, secondsToExpire);
                        }
                    }
                }finally {
                    funCacheDb.releaseLock(lockName, lock);
                }
            }else{
                log.debug("cache funArgsReturn lock no: " + key + " " + key1);
                throw new ErrorException(" no get lock and wait timeout for db date");
            }
        }else{
            log.debug("get from cache " + key + " " + key1 + " res " + res);
        }
        return res;
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
     */
    public Long initCacheFromDb(String key0, int secondsToExpire, int secondsToWait, int secondsInitDeta, FunArgsReturn<String, Map<String, Object>> getFromDbList){
        HashOperations<String,String, String> hash = redisTemplate.opsForHash();
        final String key = KEY_GET_CACHE_OR_DB + key0;
        final String keyIsOk = KEY_INIT_CACHE_OR_DB_OK + key0;
        final String lockName = KEY_LOCK_INIT_CACHE_OR_DB + key0;

        Long res = 0L;

        if(exists(keyIsOk)){	//加载过的标记还在 表示已经加载完成了
            res = hash.size(key);
        }else{	//未成功 加锁等待 执行
//					加锁 避免缓存击穿 锁粒度最小程度 key
            String lock = tryLock(lockName, secondsToExpire, secondsToWait);
            if (lock.length() > 0) {
                try {
                    if(exists(keyIsOk)){	//加载过的标记是否还在 若有则标识等待锁期间已经加载完成了 不论成功失败
                        res = hash.size(key);
                    }else{
                        Map<String, Object> keyValues = getFromDbList.make(key0);
                        if(keyValues == null || keyValues.size() == 0){	//避免缓存穿透  null是否缓存 快速过期来保护数据库  本来计划10分钟过期 则 null1分钟过期 最小5秒过期
//									布隆过滤器预热 性能实现 全局map 精确映射数据库有没有
                            log.debug("get from db " + key + " res is null ? " + (keyValues == null || keyValues.size() == 0) );
//									setMap(key, key1, res, Math.max(secondsToExpire/10, 5000));
                        }else{
                            setMap(key, keyValues, secondsToExpire);
                        }
                        set(keyIsOk, lock, secondsInitDeta);	//加载过的标记
                    }
                }finally {
                    releaseLock(lockName, lock);
                }
            }else{
                throw new ErrorException(" no get lock and wait timeout for db date init");
            }
        }
        return res;
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
     */
    public Integer setDbAndClearCache(String key0, String key1, long secondsToWait, FunArgsReturn<String, Integer> setToDb){
        final String key = KEY_GET_CACHE_OR_DB + key0;
        Integer res = 0;
        if(setToDb != null){
            res = setToDb.make(key1);	//存入数据库后，等待主从同步到从查询后再次删除，等待避免多线程查询了旧的存入了缓存 脏旧
            if(res > 0) {
                log.debug("setToDb ok size " + res);
                try {
                    log.debug(" 1 del cache key:" + key + " " + key1 + "size:" + setMap(key, key1,null, 0));
                } catch (Exception e) {
                    log.error(key + " " + key1 + " " + e.getMessage(), e);
                }
//				延迟执行 延迟队列？
                ThreadUtil.schedule(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            log.debug(" 2 del cache key:" + key + " " + key1 + "size:" + setMap(key, key1,null, 0));
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
     * 获取key的值 map
     *
     * KEY, TYPE, TTL, LEN, VALUE, EXISTS
     *
     * @param key
     */
    public Bean getKeyInfo(String key){
        Bean res = new Bean();
        res.put("KEY", key);
        if(redisTemplate.hasKey(key)) {
            res.put("EXISTS", true);
//            NONE("none"),
//            STRING("string"),
//            LIST("list"),
//            SET("set"),
//            ZSET("zset"),
//            HASH("hash");
            String type = redisTemplate.type(key).name().toLowerCase();
            res.put("TYPE", type);
            res.put("TTL", redisTemplate.getExpire(key));
            Long len = -1L;
            Object value = null;

//            @Cacheable(keyGenerator="keyGenerator",value="cache-getColsMapCache")
//          redis spring缓存问题 读取string报错 org.springframework.data.redis.serializer.SerializationException: Could not read JSON: Unexpected character ('¬' (code 172)): expected a valid value (number, String, array, object, 'true', 'false' or 'null')
//          例外处理
            if(key.startsWith("cache-")){
                value = "@Cacheable(keyGenerator=\"keyGenerator\",value=\"" + key + "\")";
                len = 998L;
            }else if (type.equals("string")) {
                try {
                    value = redisTemplate.opsForValue().get(key);
                    len = redisTemplate.opsForValue().size(key);
                }catch (Throwable e){
                    value = "Error " + e.getMessage();
                    log.error("Error redis " + type + " " + key + " " + e.getMessage(), e);
                }
            } else if (type.equals("list")) {
                len = redisTemplate.opsForList().size(key);
                value = redisTemplate.opsForList().range(key, 0, len < 50 ? -1 : 50);
            } else if (type.equals("hash")) {
                value = redisTemplate.opsForHash().entries(key);
                len = redisTemplate.opsForHash().size(key);
            } else if (type.equals("set")) {
                value = redisTemplate.opsForSet().members(key);
                len = redisTemplate.opsForSet().size(key);
            } else if (type.equals("zset")) {
                len = redisTemplate.opsForZSet().size(key);
                value = redisTemplate.opsForZSet().range(key, 0, len < 50 ? -1 : 50);
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
     * 设置key的值 map
     * KEY, TYPE, TTL, VALUE
     */
    public Bean setKeyInfo(Bean bean){
        String key = bean.get("KEY", "");
        if(key.length() == 0){
            throw new RuntimeException("key is null  " + bean);
        }
        String type = bean.get("TYPE", "").toLowerCase();
        int ttl = bean.get("TTL", 0);
        String value = bean.get("VALUE", "");

        if(type.equals("string") && redisTemplate.hasKey(key) && ! redisTemplate.type(key).name().toLowerCase().equalsIgnoreCase(type)) {
            redisTemplate.delete(key);
            log.warn("Try overwriete exiss key " + key + " value[" + getKeyInfo(key));
        }
        if (type.equals("string")) {
            redisTemplate.opsForValue().set(key, value);
        } else if (type.equals("list")) {
            redisTemplate.opsForList().leftPush(key, value);
        } else if (type.equals("hash")) {
            Map<String, String> map = JsonUtil.get(value);
            redisTemplate.opsForHash().putAll(key, map);
        } else if (type.equals("set")) {
            redisTemplate.opsForSet().add(key, value);
        } else if (type.equals("zset")) {
            redisTemplate.opsForZSet().add(key, value, TimeUtil.getCurrentTime());
        } else {
            throw new RuntimeException("Type is no of string,list,hash,set,zset  " + bean);
        }
        if(ttl > 0){
            redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
        }

        return getKeyInfo(key);
    }


}