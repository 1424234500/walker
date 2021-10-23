package com.walker.setting;

import com.walker.util.LangUtil;

/**
 * 顶级系统设置
 * jvm参数
 * -D<name>=<value>
 */
public class SystemConfig {

    public static <T> T get(String key, T defaultValue) {
        return LangUtil.turn(System.getProperty(key), defaultValue);
    }

    public static String set(String name, String value) {
        return System.setProperty(name, value);
    }


    /**
     * 获取系统设置 缓存方式
     */
    public static Boolean getCacheRedis() {
        return get("walker.cacheRedis", true);
    }

    public static Boolean setCacheRedis(Boolean redis) {
        set("walker.cacheRedis", "" + redis);
        return redis;
    }


}
