package com.walker.core.cache;

/**
 * 锁抽象协助
 */
public interface LockerHelp {
//    Logger log = LoggerFactory.getLogger(LockerBean.class);

    Boolean exists(String lockKey) throws Exception;

    Boolean compareAndSet(String lockKey, String value, Long timemillExpire) throws Exception;

    String compareAndGet(String lockKey) ;

    Boolean compareAndDelete(String lockKey, String value) throws Exception;
    Boolean delete(String lockKey) throws Exception;

}
