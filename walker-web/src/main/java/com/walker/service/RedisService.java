package com.walker.service;


import com.walker.dao.JdbcDao;
import com.walker.dao.RedisDao;
import com.walker.mode.Key;
import com.walker.util.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * redis锁管理服务 删除锁
 */
@Service("redisService")
public class RedisService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    JdbcDao jdbcDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    RedisDao redisDao;


    public List<Bean> getKeyValues(String keys){
        String key = "*";

        if(keys != null && keys.length() > 0){
            key = key + keys + key;
        }else {
            key = Key.getLockRedis(key);
        }
        Set<String> resk = redisTemplate.keys(key);
        List<Bean> res = new ArrayList<>();
        for(String str : resk){
//            Map<String, Object> map = new LinkedHashMap<>();
//            map.put("KEY", str);
//            try {
////            锁序列化导致存取问题
//                ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
//                map.put("VALUE", operations.get(str));
//            }catch (Exception e){
//                map.put("VALUE", "****");
//            }
//            res.add(map);
            res.add(redisDao.getKeyInfo(str));

        }

        return res;
    }

    public long delKeys(List<String> keys){
        Set<String> ks = new LinkedHashSet<>();
        for(String k : keys){
            if(k.startsWith(Key.getLockRedis(""))){
                ks.add(k);
                log.warn("try del the key lock : " + k);
            }else{
                log.warn("try del the key : " + k);
                ks.add(k);
            }
        }
        long res = redisTemplate.delete(ks);

        return res;
    }
    public long addLocks(String key, Object value){
        long res = 0;

        Set<String> ks = new LinkedHashSet<>();
        if(key.startsWith(Key.getLockRedis(""))){
            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            res++;
        }else{
            log.error("try add the key not lock : " + key);
        }

        return res;
    }
    public long addKeyValues(String key, Object value){
        long res = 0;

        Set<String> ks = new LinkedHashSet<>();
        ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
        operations.set(key, value);
        res++;

        return res;
    }

}
