package com.walker.socket.frame;

/**
 * 使用future模板控制socket事件启停
 * <p>
 * 底层实现详情
 */

public interface StartStop {

    /**
     * 开启监听线程
     */
    void start() throws Exception;

    /**
     * 停止监听
     * @param timemillWait 最长等待时间 kill -9
     */
    void stop(long timemillWait) throws Exception;

    boolean isStart();
}
