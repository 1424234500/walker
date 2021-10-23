package com.walker.core.cache;

import com.walker.core.aop.FunArgsReturn;

import java.util.Map;


/**
 * db持久化 抽象缓存
 */
public interface CacheDb<K> {
    <V> V getCacheOrDb(K key, Long timemillExpire, Long timemillWait, FunArgsReturn<K, V> getFromDb);
    <V> Long initCacheFromDb(Long timemillExpire, Long timemillWait, Long timemillInitDeta, FunArgsReturn<String, Map<K, V>> getFromDbList);
    Long setDbAndClearCache(String key, Long timemillWait, FunArgsReturn<K, Long> setToDb);
}
