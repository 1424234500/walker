package com.walker.mode;


import com.walker.util.TimeUtil;

import java.util.concurrent.DelayQueue;
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


    public static void main(String[] argv) throws InterruptedException {
        DelayQueue<DelayQueueItem> queue = new DelayQueue<>();
        int size = 10;
        for(int i = 0; i <size; i++) {
            for(int j = 0; j <i; j++) {
                DelayQueueItem<String> item = new DelayQueueItem<>("item" + i + "-" + j + " " + TimeUtil.getTimeYmdHms() + " -> " + TimeUtil.getTime(TimeUtil.ymdhmsS, i * 1000L), System.currentTimeMillis() + i * 1000);
                queue.add(item);
            }
        }

        for (int i = 0; i < size; i++) {
            DelayQueueItem<String> take = queue.take();
            System.out.println(" " + take.msg + " but " + TimeUtil.getTimeYmdHms());
        }



    }


}