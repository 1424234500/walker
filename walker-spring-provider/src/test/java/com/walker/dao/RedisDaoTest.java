package com.walker.dao;

import com.walker.ApplicationProviderTests;
import com.walker.box.TaskThreadPie;
import com.walker.core.util.ThreadUtil;
import com.walker.core.util.Tools;
import com.walker.spring.component.RedisLock;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

public class RedisDaoTest extends ApplicationProviderTests {
    @Autowired
    RedisLock redisLock;


    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void score() {
        String key = "", value = "";
        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
        Object o = zset.score(key, value);
        out(o);
    }

    @Test
    public void tryLock() {
        String key = "kkkk", key1 = "kk";
        String v = "", v1 = "", v2 = "";
        redisLock.releaseLock(key1, v1);
        v1 = redisLock.tryLock(key1, 5000, 1000);
        if(v1.length() > 0){
            redisLock.releaseLock(key1, v1);
        }
        v1 = redisLock.tryLock(key1, 5000, 1000);

        new TaskThreadPie(100) {
            @Override
            public void onStartThread(int threadNo) throws Exception {//抢占资源后 加锁 占用1s
                String val = "";
                try {
                    val = redisLock.tryLock(key, 5000, 1000);//可以等待1s获取锁
                    if(val.length() > 0){
                        Tools.out("t" + threadNo + " getLock ok !!!!! " + key + " " + val);
                    }

                    ThreadUtil.sleep(val.length() > 0 ? 3000 : 1000);    //有锁耗时大
                }finally {
                    if(val.length() > 0)
                        redisLock.releaseLock(key, val);
                }
            }
        }.setThreadSize(10).start();



    }

    @Test
    public void releaseLock() {
        String key = "bbbbc";
        String v = redisLock.tryLock(key, 10000, 500);
        Assert.assertTrue(v.length() > 0);
        Tools.out(v);
        Assert.assertTrue( redisLock.releaseLock(key, v) );

    }


}