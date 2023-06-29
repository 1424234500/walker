package com.walker.core.mode;

import com.alibaba.fastjson.JSON;
import com.walker.core.util.LangUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LinkedHashMap new Bean().set("key","value").set("key2","value2);
 *
 * @author Walker
 * 2018年7月13日
 */
public class BeanLinked extends LinkedHashMap<Object, Object> {
    private static final long serialVersionUID = 1L;

    /**
     * 构造体方法
     */
    public BeanLinked() {
        super();
    }

    /**
     * 构造体方法，直接初始化Bean
     *
     * @param values 带数据信息
     */
    public BeanLinked(Map<?, ?> values) {
        super(values);
    }

    /**
     * 设置对象，支持级联设置
     *
     * @param key   键值
     * @param value 对象数据
     * @return this，当前Bean
     */
    public BeanLinked set(Object key, Object value) {
        put(key, value);
        return this;
    }

    public BeanLinked put(Object key, Object value) {
        super.put(key, value);
        return this;
    }

    public BeanLinked put(String key, String value) {
        super.put(key, value);
        return this;
    }

    public BeanLinked set(String key, String value) {
        super.put(key, value);
        return this;
    }

    public BeanLinked set(String key, BeanLinked value) {
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
    public BeanLinked remove(Object key) {
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
    public BeanLinked copyOf(Object... keys) {
        BeanLinked tar = new BeanLinked();
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
