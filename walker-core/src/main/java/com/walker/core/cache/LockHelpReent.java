package com.walker.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public abstract class LockHelpReent extends CacheMap implements LockerHelp {
    private Logger log = LoggerFactory.getLogger(LockHelpReent.class);

    Map<String, ReentrantLock> reentrantLock = new LinkedHashMap<>();

    public LockHelpReent() {
        ;
    }

    @Override
    public Boolean exists(String lockKey) throws Exception {
        return getLock(lockKey).isLocked();
    }

    @Override
    public Boolean compareAndSet(String lockKey, String value, Long timemillExpire) throws Exception {
        return getLock(lockKey).tryLock(timemillExpire, TimeUnit.MILLISECONDS);
    }

    private ReentrantLock getLock(String lockKey) {
        ReentrantLock r = reentrantLock.get(lockKey);
        if(r == null) {
            r = new ReentrantLock();
            reentrantLock.put(lockKey, r);
        }
        return r;
    }

    @Override
    public String compareAndGet(String lockKey) {
        return getLock(lockKey).getHoldCount() + "";
    }

    @Override
    public Boolean compareAndDelete(String lockKey, String identifier) throws Exception {
        getLock(lockKey).unlock();
        return true;
    }
    @Override
    public Boolean delete(String lockKey) throws Exception {
        getLock(lockKey).unlock();
        return true;
    }
}