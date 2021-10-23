package com.walker.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试多线程累加线程安全
 */
public class TestThreadAdd {


    int threadSize = 10;
    int size = 100000;
    int count = 0;
    volatile int countV = 0;
    AtomicInteger countCas = new AtomicInteger(0);

    TestThreadAdd() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            testAdd(true);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new TestThreadAdd();

    }

    public void testAdd(boolean iff) throws InterruptedException {
        count = 0;
        countV = 0;
        countCas = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(threadSize);
        for (int i = 0; i < size; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    count++;
                    countV++;
                    countCas.addAndGet(1);
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10 * 1000, TimeUnit.MILLISECONDS);

        System.out.println("size " + size + " res " + count + " v " + countV + " cas " + countCas);
    }


}
