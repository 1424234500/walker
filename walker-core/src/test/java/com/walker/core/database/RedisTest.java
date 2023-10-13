package com.walker.core.database;

import com.walker.core.util.Tools;
import org.junit.Assert;
import org.junit.Test;

public class RedisTest {

    @Test
    public void testConf() throws Exception {
        Redis connector = Redis.getInstance();
        connector.init();
        connector.check();
        connector.uninit();
    }
    @Test
    public void testModel(){
        Redis redis = new Redis().setHost("127.0.0.1:6379").setPassword("");
        String res = redis.doJedis(jedis -> {
            jedis.set("hello", "world");
            return jedis.get("hello");
        });
        Tools.out(res);
        Assert.assertTrue(res != null);
    }


}
