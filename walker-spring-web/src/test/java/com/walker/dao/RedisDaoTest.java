package com.walker.dao;

import com.walker.ApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class RedisDaoTest extends ApplicationTests {

    @Autowired
    RedisDao redisDao;

    @Test
    public void getMap() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("v", "vv");

        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        map.put("key2", "value2");
        map.put("key3", map1);

        redisDao.setMap("test:map1", "kkk", "vvv", 1000 * 100);
        redisDao.getMap("test:map1", "kkk", "vvv");
        redisDao.setMap("test:map", map, 1000 * 100);
        out(redisDao.getMap("test:map", "key", "defaultv"));
        out(redisDao.getMap("test:map", "key3", ""));




    }
}