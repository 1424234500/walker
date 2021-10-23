package com.walker.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public abstract class LockReent extends CacheMap {
    private Logger log = LoggerFactory.getLogger(LockReent.class);

    ReentrantLock reentrantLock;

    public LockReent(ReentrantLock reentrantLock) {
        this.reentrantLock = reentrantLock;
    }

    @Override
    public Boolean exists(String lockKey) throws Exception {
        return reentrantLock.isLocked();
    }

    @Override
    public Boolean compareAndSet(String lockKey, String value, Long timemillExpire) throws Exception {
        return reentrantLock.tryLock(timemillExpire, TimeUnit.MILLISECONDS);
    }


    @Override
    public Boolean compareAndDelete(String lockKey, String identifier) throws Exception {
        reentrantLock.unlock();
        return true;
    }
}