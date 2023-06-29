package com.walker.dao;

import com.walker.core.aop.FunArgsReturn;
import com.walker.core.database.SqlUtil;
import com.walker.core.util.LangUtil;
import com.walker.core.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 配置抽象
 *
 *  ID VALUE   ABOUT   S_FLAG  S_MTIME
 *
 */
@Repository
public class ConfigDao {
    private static Logger log = LoggerFactory.getLogger(ConfigDao.class);

    @Autowired
    RedisDao redisDao;
    @Autowired
    JdbcDao jdbcDao;

    String CONF_ID = "config";


    private static final AtomicInteger count = new AtomicInteger(0);

    /**
     * 从数据库初始化 预热
     * 避免集体失效 分布过期时间
     */
    public int reload(){
        int res = -1;
//        String key0, long millisecondsToExpire, long millisecondsToWait, long initDeta, FunArgsReturnBool<String, Map<String, Object
        res = redisDao.initCacheFromDb(CONF_ID, 24 * 3600, 30, 10 * 60, new FunArgsReturn<String, Map<String, Object>>() {
            @Override
            public Map<String, Object> make(String obj) {
                Map<String, Object> res = new LinkedHashMap<>();

                List<Map<String, Object>> line = jdbcDao.find("select ID, VALUE from W_SYS_CONFIG where S_FLAG='1' order by S_MTIME ");
                for(Map<String, Object> item : line){
                    log.debug(item.toString());
                    res.put(String.valueOf(item.get("ID")), item.get("VALUE"));
                }
                log.info(" load cache " + count.addAndGet(1) + " " + CONF_ID + " " + res );    //注意确保单例问题

                return res;
            }
        }).intValue();


        return res;
    }


    /**
     *
     * 穿透？  等待锁超时怎么办   熔断怎么办 宕机
     *
     * 并发访问 等待获取数据库 加锁 等待多一些时间
     *
     * 配置缓存10小时 或者永不过期？ 等待变化时通知清理缓存
     * 并发访问配置时 多等待充足时间 等待其他进程线程查询 10s
     *
     * @param key
     * @param defaultValue
     * @param <T>
     */
    public <T> T get(String key, T defaultValue){

//        String res = redisDao.getCacheOrDb(CONF_ID, key, 3600, 8, new FunCacheDb<String, String>() {
//            @Override
//            public boolean setCache(String key, String valueFromDb, int expireSec) {
//                long res = redisDao.setMap(CONF_ID, key, valueFromDb, expireSec);
//                return res > 0;
//            }
//
//            @Override
//            public String getCache(String key) {
//                return (String) redisDao.getMap(CONF_ID, key, defaultValue);
//            }
//
//            @Override
//            public String getDb(String key) {
//                String v = "";
//                Map<String, Object> line = jdbcDao.findOne("select VALUE from W_SYS_CONFIG where ID=? and S_FLAG='1' ", key);
//                if(line != null){
//                    Object vv = line.get("VALUE");
//                    v = String.valueOf(vv);
//                }
//                return v;            }
//
//            @Override
//            public boolean setDb(String key) {
//                return false;
//            }
//
//            @Override
//            public String tryLock(String lockName, int secondsToExpire, int secondsToWait) {
//                return redisDao.tryLock(lockName, secondsToExpire, secondsToWait);
//            }
//
//            @Override
//            public boolean releaseLock(String lockName, String identifier) {
//                return redisDao.releaseLock(lockName, identifier);
//            }
//        });




        String res = redisDao.getCacheOrDb(CONF_ID, key, 3600, 8, new FunArgsReturn<String, String>() {
            @Override
            public String make(String obj) {
                String v = null;
                Map<String, Object> line = jdbcDao.findOne("select VALUE from W_SYS_CONFIG where ID=? and S_FLAG='1' ", obj);
                if(line != null){
                    Object vv = line.get("VALUE");
                    v = String.valueOf(vv);
                }
                log.info("load cache from db " + obj + " v:" + v);
                return v;
            }
        });

        return LangUtil.turn(res, defaultValue);
    }

    public Integer set(String ID, String VALUE, String ABOUT){
        return set(ID, VALUE, ABOUT, "1", "");
    }

    /**
     * 删除缓存 删除db
     */
    public Integer set(String ID, FunArgsReturn<String, Integer> call){
        return redisDao.setDbAndClearCache(CONF_ID, ID, 5, call);
    }
    public Integer set(String ID, String VALUE, String ABOUT, String S_FLAG, String S_MTIME){

        Integer res = redisDao.setDbAndClearCache(CONF_ID, ID, 5,  new FunArgsReturn<String, Integer>() {
            @Override
            public Integer make(String obj) {
                Integer rr = 0;
                boolean isexist = false;
                Map<String, Object> line = jdbcDao.findOne("select * from W_SYS_CONFIG where ID=?  ", ID);
                if(line != null) {
                    isexist = true;
                }
                if(line == null){
                    line = new HashMap<>();
                }
                isexist = true;
                if(VALUE != null ){
                    line.put("VALUE", VALUE);
                }
                if(ABOUT != null ){
                    line.put("ABOUT", ABOUT);
                }
                if(S_FLAG != null ){
                    line.put("S_FLAG", S_FLAG);
                }
                if(S_MTIME != null ){
                    line.put("S_MTIME", S_MTIME);
                }else{
                    line.put("S_MTIME", TimeUtil.getTimeYmdHmss());
                }
                if(isexist){
                    rr = jdbcDao.executeSql("update W_SYS_CONFIG set ID=?,VALUE=?,ABOUT=?,S_FLAG=?,S_MTIME=? where ID=? ", line.get("ID"), line.get("VALUE"), line.get("ABOUT"), line.get("S_FLAG"), line.get("S_MTIME"), line.get("ID"));
                }else{
                    rr = jdbcDao.executeSql("insert into W_SYS_CONFIG values(" + SqlUtil.makePosition("?", line.size()) + ")", line.get("ID"), line.get("VALUE"), line.get("ABOUT"), line.get("S_FLAG"), line.get("S_MTIME") );
                }
                return  (rr);
            }
        });

        return res;
    }










}
