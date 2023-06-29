package com.walker.core.cache;

import com.walker.core.util.LangUtil;
import com.walker.core.util.TimeUtil;
import com.walker.core.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 锁抽象适配
 *  依赖 LockerHelp
 *
 *
 * 具体实现
 *  jvm
 *  redis
 *  zookeeper
 *
 * Locker l = new LockAdapter<LockHelpRedis>(  )
 */
public abstract class LockAdapter extends CacheDbAdapter implements Locker{
    private Logger log = LoggerFactory.getLogger(LockAdapter.class);


    private static final int MAX_WHILE = 3;
    private static final String KEY= "/lock";
    private static final String KEY_LOCK = KEY + "/";
    private static final AtomicLong lockNo = new AtomicLong(0);

    @Override
    public String tryLock(String lockName, Long timemillExpire, Long timemillWait) {
        String lockKey =  KEY_LOCK + lockName;
        String value = Thread.currentThread().getName() + ":" + LangUtil.getTimeSeqId() + ":" + lockNo + "@" + TimeUtil.getTime(System.currentTimeMillis() + timemillExpire, TimeUtil.seq1);
        long startTime = System.currentTimeMillis();
        String result = null;

        int cc = 0;
        while(cc++ <= MAX_WHILE) {	//自循环等待 硬限制最多1000次
            try {
//                if(cc <= 1 && ! exists(lockKey)){
//                    compareAndSet(lockKey, "root lock", 1000 * 24 * 3600 * 365L);
//                }

                if( ! exists(lockKey) && compareAndSet(lockKey, value, timemillExpire)) {
                    lockNo.addAndGet(1);
                    log.debug(Tools.objects2string("tryLock ok", cc, lockKey, lockName, value, result, timemillExpire, timemillWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime)), lockNo.get());
                    result = value;
                    break;
                }
            } catch (Exception e) {
                if(cc >= MAX_WHILE - 1 || cc <= 1)
                   log.error(Tools.objects2string("tryLock exception", cc, lockKey, lockName,value, result, timemillExpire, timemillWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime), lockNo.get(), e.getMessage()), e);
            }
            if(System.currentTimeMillis() > startTime + timemillExpire){
                log.warn(Tools.objects2string("tryLock error wait timeout", cc, lockKey, lockName,value, result, timemillExpire, timemillWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime), "now locked on", compareAndGet(lockKey) , lockNo.get()));
                break;
            }
            try {//1000ms等待锁，共轮询10次
                TimeUnit.MILLISECONDS.sleep(Math.max(timemillWait/10, 10));
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        if(cc >= MAX_WHILE){
            log.warn(Tools.objects2string("max while true count", cc, lockKey, lockName,value, result, timemillExpire, timemillWait, "startTimeAt", TimeUtil.getTimeYmdHmss(startTime), "now locked on", compareAndGet(lockKey) , lockNo.get()));
        }
        return result;
    }

    @Override
    public String releaseLock(String lockName, String identifier) {
        String lockKey =  KEY_LOCK + lockName;
        String res = null;

        try {
            if(exists(lockKey)){
                if(compareAndDelete(lockKey, identifier)){
                    log.debug(Tools.objects2string("releaseLock ok", lockKey, lockName, identifier));
                }else{
                    res = Tools.objects2string("releaseLock error have timeout?", lockKey, lockName, identifier, "lockRes:" + false, "but identifier should be " + compareAndGet(lockKey));
                    log.error(res);
                }
            }
        }catch (Exception e){
            res = Tools.objects2string("releaseLock exception", lockKey, lockName, identifier, e.getMessage());
            log.error(res, e);
        }
        return res;
    }
    @Override
    public String releaseLockForce(String lockName){
        String lockKey =  KEY_LOCK + lockName;
        String res = null;
        String identifier = null;
        try {
            if(exists(lockKey)){
                if(delete(lockKey)){
                    log.debug(Tools.objects2string("releaseLockForce ok", lockKey, lockName, identifier));
                }else{
                    res = Tools.objects2string("releaseLockForce error have timeout?", lockKey, lockName, identifier, "lockRes:" + false, "but identifier should be " + compareAndGet(lockKey));
                    log.error(res);
                }
            }
        }catch (Exception e){
            res = Tools.objects2string("releaseLockForce exception", lockKey, lockName, identifier, e.getMessage());
            log.error(res, e);
        }
        return res;
    }

    @Override
    public Boolean isLocked(String lockName) {
        String lockKey =  KEY_LOCK + lockName;

        try {
            return exists(lockKey);
        } catch (Exception e) {
            log.error("isLocked " + lockName + " " + lockKey + " " + e.getMessage(), e);
        }
        return true;
    }
}
