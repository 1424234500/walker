package com.walker.util;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap子类 new Bean().set("key","value").set("key2","value2);
 *
 * @author Walker
 * 2018年7月13日
 */
public class Bean extends HashMap<Object, Object> {
    private static final long serialVersionUID = 1L;

    /**
     * 构造体方法
     */
    public Bean() {
        super();
    }

    /**
     * 构造体方法，直接初始化Bean
     *
     * @param values 带数据信息
     */
    public Bean(Map<?, ?> values) {
        super(values);
    }

    /**
     * 设置对象，支持级联设置
     *
     * @param key   键值
     * @param value 对象数据
     * @return this，当前Bean
     */
    public Bean set(Object key, Object value) {
        put(key, value);
        return this;
    }

    public Bean put(Object key, Object value) {
        super.put(key, value);
        return this;
    }

    public Bean put(String key, String value) {
        super.put(key, value);
        return this;
    }

    public Bean set(String key, String value) {
        super.put(key, value);
        return this;
    }

    public Bean set(String key, Bean value) {
        super.put(key, value);
        return this;
    }

    /**
     * 获取对象值，如果不存在则返回缺省对象
     *
     * @param key          键值
     * @param defaultValue 缺省对象
     * @return 对象值
     */
    public <T> T get(Object key, T defaultValue) {
        Object obj = get(key);

        T res = LangUtil.turn(obj, defaultValue);

        return res;
    }


    /**
     * 删除指定值
     *
     * @param key 键值
     * @return 当前对象
     */
    public Bean remove(Object key) {
        if (containsKey(key)) {
            super.remove(key);
        }
        return this;
    }


    /**
     * 将在数组中设定属性键值的内容传递到另外一个bean
     *
     * @param keys 键值数组 null表示传全部src中的数据
     * @return 复制出来的数据内容，
     */
    public Bean copyOf(Object... keys) {
        Bean tar = new Bean();
        if (keys != null) {
            for (Object key : keys) {
                tar.set(key, get(key));
            }
        } else {
            tar.putAll(this);
        }
        return tar;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }


}
