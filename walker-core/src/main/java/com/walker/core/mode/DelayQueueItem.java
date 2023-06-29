package com.walker.core.mode;


import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 *
 * 延迟队列 线程池 测试
 *
 */
public class DelayQueueItem<MSG> implements Delayed {
    /**
     * 触发时间
     */
    long onTimeMill;
    /**
     * 消息体
     */
    MSG msg;

    public DelayQueueItem(MSG msg, long onTimeMill) {
        this.msg = msg;
        this.onTimeMill = onTimeMill;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return this.onTimeMill - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        DelayQueueItem item = (DelayQueueItem) o;
        long diff = this.onTimeMill - item.onTimeMill;
        if (diff <= 0) {// 改成>=会造成问题
            return -1;
        }else {
            return 1;
        }
    }



}