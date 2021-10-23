package com.walker.core.database;

import com.walker.core.cache.ConfigMgr;
import com.walker.core.cache.LockHelpReent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存缓存 map接入
 */
public class MemCache extends LockHelpReent {
    private final static Logger log = LoggerFactory.getLogger(MemCache.class);
    /**
     * 存储缓存
     */
    private final Map<String, Object> map = new ConcurrentHashMap<>();
    private final Map<String, Index> mapIndex = new ConcurrentHashMap<>();
    protected Integer size;

    /**
     * 单例模式
     */
    public static MemCache getInstance() {
        return SingletonFactory.instance;
    }

    @Override
    public Map<String, Object> getMap() {
        return map;
    }

    @Override
    public Map<String, Index> getMapIndex() {
        return mapIndex;
    }

    @Override
    public String info() {
        return this.toString();
    }

    @Override
    public Boolean doTest() throws Exception {
        put("hello", "world");
        String res = get("hello", "1");
        log.info("test res " + res);
        return res.equals("world");
    }

    @Override
    public Boolean doInit() throws Exception {
        return super.doInit();
    }

    @Override
    public Boolean doUninit() throws Exception {
        map.clear();
        mapIndex.clear();
        return super.doUninit();
    }

    public Integer getSize() {
        return size;
    }

    public MemCache setSize(Integer size) {
        this.size = size;
        return this;
    }

    private static class SingletonFactory {
        static MemCache instance;

        static {
            ConfigMgr cache = ConfigMgr.getInstance();

            log.warn("singleton instance construct " + SingletonFactory.class);

            instance = new MemCache();
            instance
                    .setSize(cache.get("MemCache.size", Integer.MAX_VALUE))
            ;
        }
    }
}
