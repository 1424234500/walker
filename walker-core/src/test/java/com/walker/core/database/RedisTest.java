package com.walker.core.database;

import com.walker.core.aop.TestModel;
import com.walker.core.util.Tools;
import org.junit.Assert;
import org.junit.Test;

public class RedisTest {

    @Test
    public void testConf(){
        TestModel testModel = Redis.getInstance();
        testModel.init();
        testModel.test();
        testModel.uninit();
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
