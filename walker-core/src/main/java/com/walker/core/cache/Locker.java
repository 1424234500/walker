package com.walker.core.cache;

/**
 * 锁抽象
 * 具体实现
 *  jvm
 *  redis
 *  zookeeper
 *
 *
 */
public interface Locker {

    /**
     * 加锁 自行实现 compare and set 保证
     * @param lockName 锁url
     * @param timemillExpire 超时释放ms
     * @param timemillWait 等待加锁时间ms
     * @return not null = 解锁url的秘钥   null = 加锁失败
     */
    String tryLock(String lockName, Long timemillExpire, Long timemillWait);

    /**
     * 解锁
     * @param lockName 锁url
     * @param identifier 解锁url的秘钥
     * @return not null = 解锁失败原因    null = 解锁成功
     */
    String releaseLock(String lockName, String identifier);
    /**
     * 强制解锁
     * @param lockName 锁url
     * @return not null = 解锁失败原因    null = 解锁成功
     */
    String releaseLockForce(String lockName);
    /**
     *  检查锁
     * @param lockName
     * @return true 存在 or 异常则
     */
    Boolean isLocked(String lockName);



}
