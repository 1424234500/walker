package com.walker.core.cache;

/**
 * 组合cache locker LockerHelp
 * @param <K>
 */
public interface CacheLocker<K> extends Cache<K>, CacheDb<K>, Locker, LockerHelp {

}
