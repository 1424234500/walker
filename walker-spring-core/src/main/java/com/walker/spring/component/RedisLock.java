package com.walker.spring.component;

import com.walker.core.mode.Key;
import com.walker.core.util.LangUtil;
import com.walker.core.util.TimeUtil;
import com.walker.core.util.Tools;
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
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * 常用redis操作 template实现
 */
@Repository
public class RedisLock {
    final static String UNLOCK_LUA = "" +
            "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
            "then " +
            "    return redis.call(\"del\",KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end ";
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 尝试Lock 成功后返回密钥\
     * <p>
     * <p>
     * 1/ 非重入，等待锁时使用线程sleep
     * <p>
     * 2/使用  redis的  SETNX   带过期时间的方法
     * <p>
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
        while (cc++ < 1000) {    //自循环等待 硬限制最多1000次
            try {
//        		String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
                result = redisTemplate.execute(new RedisCallback() {
                    @Override
                    public Object doInRedis(RedisConnection connection) throws DataAccessException {
                        try {
                            Object nativeConnection = connection.getNativeConnection();
                            String resultString = "";
//                            JedisCommands commands = (JedisCommands) connection.getNativeConnection();
//                            resultString = commands.set(lockKey, value, "NX", "PX", secondsToExpire);
                            if (nativeConnection instanceof RedisAsyncCommands) {
                                RedisAsyncCommands command = (RedisAsyncCommands) nativeConnection;
                                resultString = command
                                        .getStatefulConnection()
                                        .sync()
                                        .set(keyByte, valueByte, SetArgs.Builder.nx().ex(secondsToExpire));
                            } else if (nativeConnection instanceof RedisAdvancedClusterAsyncCommands) {
                                RedisAdvancedClusterAsyncCommands clusterAsyncCommands = (RedisAdvancedClusterAsyncCommands) nativeConnection;
                                resultString = clusterAsyncCommands
                                        .getStatefulConnection()
                                        .sync()
                                        .set(keyByte, valueByte, SetArgs.Builder.nx().ex(secondsToExpire));
                            }
                            return resultString;
                        } finally {
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
            if (System.currentTimeMillis() > startTime + secondsToWait * 1000) {
                log.warn(Tools.objects2string("tryLock error wait timeout", cc, lockKey, lockName, value, result, secondsToExpire, secondsToWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)));
                break;
            }
            try {//1000ms等待锁，共轮询10次
                TimeUnit.MILLISECONDS.sleep(Math.max(secondsToWait * 1000 / 100, (long) (Math.random() * 40 + 10)));
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
     *
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
                try {
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
                        RedisScriptingAsyncCommands<Object, byte[]> command = (RedisScriptingAsyncCommands<Object, byte[]>) nativeConnection;
                        RedisFuture future = command.eval(UNLOCK_LUA, ScriptOutputType.INTEGER, keyParam, valueBytes);
                        try {
                            return (Long) future.get();
                        } catch (InterruptedException e) {
                            return -1L;
                        } catch (ExecutionException e) {
                            return -2L;
                        }
                    }
                    return 0L;
                } finally {
                    connection.close();
                }
            }
        });
        boolean res = result != null && (Long) result > 0;
        if (res) {
            log.debug(Tools.objects2string("release lock ok", lockKey, lockName, identifier, result));
        } else {
            Object v = redisTemplate.opsForValue().get(lockKey);
            log.error(Tools.objects2string("release lock error", lockKey, lockName, identifier, result, "value should be", v));
        }
        return res;
    }


    /**
     * 查看是否加锁
     *
     * @param key
     */
    public boolean isLocked(String key) {
        Object o = redisTemplate.opsForValue().get(key);
        return o != null;
    }

//    /**
//     * 获取key的值 map
//     * <p>
//     * KEY, TYPE, TTL, LEN, VALUE, EXISTS
//     *
//     * @param key
//     */
//    public Bean getKeyInfo(String key) {
//        Bean res = new Bean();
//        res.put("KEY", key);
//        if (redisTemplate.hasKey(key)) {
//            res.put("EXISTS", true);
////            NONE("none"),
////            STRING("string"),
////            LIST("list"),
////            SET("set"),
////            ZSET("zset"),
////            HASH("hash");
//            String type = redisTemplate.type(key).name().toLowerCase();
//            res.put("TYPE", type);
//            res.put("TTL", redisTemplate.getExpire(key));
//            Long len = -1L;
//            Object value = null;
//            if (type.equals("string")) {
//                value = redisTemplate.opsForValue().get(key);
//                len = redisTemplate.opsForValue().size(key);
//            } else if (type.equals("list")) {
//                len = redisTemplate.opsForList().size(key);
//                value = redisTemplate.opsForList().range(key, 0, len < 50 ? -1 : 50);
//            } else if (type.equals("hash")) {
//                value = redisTemplate.opsForHash().entries(key);
//                len = redisTemplate.opsForHash().size(key);
//            } else if (type.equals("set")) {
//                value = redisTemplate.opsForSet().members(key);
//                len = redisTemplate.opsForSet().size(key);
//            } else if (type.equals("zset")) {
//                len = redisTemplate.opsForZSet().size(key);
//                value = redisTemplate.opsForZSet().range(key, 0, len < 50 ? -1 : 50);
//            } else {
//                value = "none";
//                res.put("VALUE", "none type");
//            }
//            res.put("VALUE", value);
//            res.put("LEN", len);
//
//        } else {
//            res.put("EXISTS", false);
//        }
//        return res;
//    }
//

}