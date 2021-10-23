package com.walker.core.encode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * fastjson 对象 序列化存取
 */
public class JsonFastUtil {

    public static <T> String toStringFormat(T obj) {
        return JSON.toJSONString(obj, SerializerFeature.PrettyFormat);
    }

    public static <T> String toString(T obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T get(String str) {
        return JSON.parseObject(str, new TypeReference<T>() {
        });
    }

    public static <T> T get(String str, TypeReference<T> t) {
        return JSON.parseObject(str, t);
    }

    public static <T> T get(String str, Class<T> clz) {
        return JSON.parseObject(str, clz);
    }

    public static <T> T get1(String str, TypeReference<T> t) {
        return JSON.parseObject(str, new TypeReference<T>() {
        });
    }


}
