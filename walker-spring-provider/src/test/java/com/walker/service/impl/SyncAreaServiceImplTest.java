package com.walker.service.impl;

import com.walker.ApplicationProviderTests;
import com.walker.mode.school.Area;
import com.walker.mode.Key;
import com.walker.service.SyncAreaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;


public class SyncAreaServiceImplTest extends ApplicationProviderTests {
    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    SyncAreaService syncAreaService;

    Area area = new Area().setUrl("http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/22.html");

    @Test
    public void isExists() {
        out(area, syncAreaService.isExists(area));
        syncAreaService.setDone(area);
        out(area, syncAreaService.isExists(area));



    }

    @Test
    public void setDone() {

        boolean res = false;

        String key = Key.getUrlDone();
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
        zset.add(key, area.getUrl(), 20);
        double score = zset.score(key, area.getUrl());
        res = score > 0;
        out(res);
        out( zset.score(key, "aasdfasdf"));



    }
}