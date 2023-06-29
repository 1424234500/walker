package com.walker.core.cache;

import com.walker.core.aop.FunArgsReturn;
import com.walker.core.database.ZooKeeperUtil;
import com.walker.core.mode.CacheModelZk;
import com.walker.core.mode.Page;
import com.walker.core.mode.SqlColumn;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class CacheZookeeper extends LockAdapter implements Cache<String> {
    private Logger log = LoggerFactory.getLogger(CacheZookeeper.class);

    protected abstract <T> T doZooKeeper(FunArgsReturn<ZooKeeper, T> fun);


    @Override
    public List<SqlColumn> getCols() {
        return CacheModelZk.colMaps;
    }

    @Override
    public List<CacheModelZk> findPage(String key, Page page) {
        return doZooKeeper(zooKeeper -> ZooKeeperUtil.findPage(zooKeeper, false, key, page));
    }


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