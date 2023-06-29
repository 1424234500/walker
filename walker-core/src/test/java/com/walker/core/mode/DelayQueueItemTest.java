package com.walker.core.mode;

import com.walker.core.util.TimeUtil;
import junit.framework.TestCase;

import java.util.concurrent.DelayQueue;

public class DelayQueueItemTest extends TestCase {

    public void testGetDelay() throws InterruptedException {
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