package com.walker.core.cache;

import com.walker.core.database.RedisUtil;

public abstract class LockHelpRedis extends CacheRedis implements LockerHelp{

    @Override
    public Boolean exists(String lockKey) throws Exception {
        return doJedis(jedis -> {
            try {
                return RedisUtil.exists(jedis, lockKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Boolean compareAndSet(String lockKey, String value, Long timemillExpire) throws Exception {
//        timemillExpire = timemillExpire / 1000 + (long) (Math.ceil(timemillExpire % 1000 * 1f / 1000));
        return doJedis(jedis -> {
            try {
                return RedisUtil.compareAndSet(jedis, lockKey, value, timemillExpire);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public String compareAndGet(String lockKey)  {
        return doJedis(jedis -> {
            try {
                return RedisUtil.compareAndGet(jedis, lockKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Boolean compareAndDelete(String lockKey, String identifier) throws Exception {
        return doJedis(jedis -> {
            try {
                return RedisUtil.compareAndDelete(jedis, lockKey, identifier);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    @Override
    public Boolean delete(String lockKey) throws Exception {
        return doJedis(jedis -> {
            try {
                return RedisUtil.delete(jedis, lockKey) > 0;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}