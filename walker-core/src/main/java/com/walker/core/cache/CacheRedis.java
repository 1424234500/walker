package com.walker.core.cache;

import com.walker.core.database.Redis;
import com.walker.core.database.RedisUtil;
import com.walker.mode.CacheModelRedis;
import com.walker.mode.Page;

import java.util.List;

/**
 * 缓存服务实现类
 *  依赖具体缓存模型 doJedis
 *
 * redis实现
 * 键值 序列化
 */
public abstract class CacheRedis extends LockAdapter implements Cache<String> {

    public abstract <T> T doJedis(Redis.Fun<T> fun);


    @Override
    public List<CacheModelRedis> findPage(String key, Page page) {
        return doJedis(jedis -> RedisUtil.findPage(jedis, key, page));
    }


    @Override
    public Long size() {
        return doJedis(jedis -> jedis.dbSize());
    }


    @Override
    public <V> V get(String key, V defaultValue) {
        return doJedis(jedis ->  RedisUtil.get(jedis, key, defaultValue));
    }

    @Override
    public <V> Cache<String> put(String key, V value, Long timemillExpire) {
        doJedis(jedis -> {
            RedisUtil.set(jedis, key, value, timemillExpire);
            return null;
        });
        return this;
    }

    @Override
    public Long remove(String key) {
        return doJedis(jedis -> RedisUtil.delete(jedis, key));
    }
}
