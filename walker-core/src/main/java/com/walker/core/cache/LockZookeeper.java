package com.walker.core.cache;

import com.walker.core.aop.FunArgsReturn;
import com.walker.core.database.ZooKeeperUtil;
import org.apache.zookeeper.ZooKeeper;

/**
 * 缓存服务实现类
 * redis实现
 * 键值 序列化
 */
public abstract class LockZookeeper extends CacheZookeeper {

    public abstract <T> T doZooKeeper(FunArgsReturn<ZooKeeper, T> fun);

    @Override
    public Long size() {
        return 0L;
    }


    @Override
    public <V> V get(String key, V defaultValue) {
        return doZooKeeper(zooKeeper -> ZooKeeperUtil.get(zooKeeper, false, key, defaultValue));
    }

    @Override
    public <V> Cache<String> put(String key, V value, Long timemillExpire) {
        doZooKeeper(zooKeeper -> ZooKeeperUtil.createOrUpdateVersion(zooKeeper, false, key, String.valueOf(value)));
        return this;
    }

    @Override
    public Long remove(String key) {
        return doZooKeeper(zooKeeper -> ZooKeeperUtil.delete(zooKeeper, false, key));
    }
}
