package com.walker.dao;

public interface Limiter {
    String tryAcquire(String url, String userId);

    /**
     * 从数据库初始化 预热
     * 避免集体失效 分布过期时间
     */
    int reload();


}
