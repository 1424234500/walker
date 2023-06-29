package com.walker.core.cache;

import com.walker.core.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public abstract class LockRedis extends CacheRedis {
    private Logger log = LoggerFactory.getLogger(LockRedis.class);



    @Override
    public Boolean exists(String lockKey) throws Exception {
        return doJedis(jedis -> {
            return jedis.exists(lockKey);
        });
    }

    @Override
    public Boolean compareAndSet(String lockKey, String value, Long timemillExpire) throws Exception {
//        timemillExpire = timemillExpire / 1000 + (long) (Math.ceil(timemillExpire % 1000 * 1f / 1000));
        return doJedis(jedis -> {
            String res = jedis.set(lockKey, value, "NX", "PX", timemillExpire);
            Tools.out(res);
            return res != null && res.length() > 0 && res.toLowerCase().equals("ok");
        });
    }


    @Override
    public Boolean compareAndDelete(String lockKey, String identifier) throws Exception {
        return doJedis(jedis -> {
            Boolean res = false;
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(identifier));
            if (! String.valueOf(result).equalsIgnoreCase("0") ) {
                return true;
            }
            return res;
        });
    }
}