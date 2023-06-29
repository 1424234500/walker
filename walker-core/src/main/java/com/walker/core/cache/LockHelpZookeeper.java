package com.walker.core.cache;

import com.walker.core.database.ZooKeeperUtil;
import com.walker.core.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存服务实现类
 * redis实现
 * 键值 序列化
 */
public abstract class LockHelpZookeeper extends CacheZookeeper implements LockerHelp {
    private Logger log = LoggerFactory.getLogger(LockHelpZookeeper.class);


    @Override
    public Boolean exists(String lockKey) throws Exception {
        return doZooKeeper(zooKeeper -> {
            try {
//                过期问题
                String str = ZooKeeperUtil.get(zooKeeper, false, lockKey, null);
                if(str != null){
                    if(str.indexOf('@') >= 0){
                        str = str.substring(str.indexOf('@'));
                        if(str.compareTo(TimeUtil.getTime(System.currentTimeMillis(), TimeUtil.seq1)) >= 0){
                            ZooKeeperUtil.delete(zooKeeper, false, lockKey);
                            str = null;
                        }
                    }
                }

                return str != null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Boolean compareAndSet(String lockKey, String value, Long timemillExpire) {
        return doZooKeeper(zooKeeper -> ZooKeeperUtil.create(zooKeeper, false, lockKey, value));
    }

    @Override
    public String compareAndGet(String lockKey) {
        return doZooKeeper(zooKeeper -> ZooKeeperUtil.get(zooKeeper, false, lockKey, null));
    }

    @Override
    public Boolean compareAndDelete(String lockKey, String value) {
        return doZooKeeper(zooKeeper -> ZooKeeperUtil.compareAndDelete(zooKeeper, lockKey, value));
    }

    @Override
    public Boolean delete(String lockKey) throws Exception {
        return doZooKeeper(zooKeeper -> ZooKeeperUtil.delete(zooKeeper, false, lockKey) > 0);
    }
}