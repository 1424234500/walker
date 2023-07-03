package com.walker.demo.lock;


import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 数组遍历，四个线程协作处理
 */
public class AgLockSync {


    /**
     * 可见性问题 一个线程修改后 其他线程立即?可见
     */
    volatile int indexAllNow = 0;
    Object lock = new Object();

    AgLockSync() throws Exception {
        int nThreads = 4;
        final int size = 1000;
//        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        ThreadPoolExecutor pool = new ThreadPoolExecutor(nThreads, nThreads, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1000));
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i;
        }
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < nThreads; i++) {
            final int now = i;
            pool.execute(new Runnable() {
                final List<Integer> my = new ArrayList<>();

                @SneakyThrows
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && indexAllNow < size) {
                        synchronized (lock) {
                            if (indexAllNow % nThreads == now) { //当前线程执行权
                                my.add(indexAllNow);
                                indexAllNow++; //切换执行权
                                lock.notifyAll(); //反复唤醒随机性导致浪费? 定向唤醒?
                            } else {
                                lock.wait(); //释放锁 等待
                            }
                        }
                    }
                    System.out.println("" + Thread.currentThread().getName() + " size " + my.size() + my.subList(0, Math.min(my.size(), 10)));
                }
            });
        }
        pool.shutdown();
        pool.awaitTermination(10000, TimeUnit.MILLISECONDS);
        List<Runnable> last = pool.shutdownNow();
        System.out.println("last runnable " + last);
        System.out.println(" cost all 1 " + (System.currentTimeMillis() - startTime));

        indexAllNow = 0;
        ThreadPoolExecutor pool2 = new ThreadPoolExecutor(nThreads, nThreads, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1000));

        startTime = System.currentTimeMillis();
        for (int i = 0; i < nThreads; i++) {
            final int now = i;
            pool2.execute(new Runnable() {
                final List<Integer> my = new ArrayList<>();

                @SneakyThrows
                @Override
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && indexAllNow < size) {
                        if (indexAllNow % nThreads == now) {
//                            当前线程执行权限
                            my.add(indexAllNow);
                            indexAllNow++;
                        } else {
                            Thread.sleep(1);
                        }

                    }
                    System.out.println("" + Thread.currentThread().getName() + " size " + my.size() + my.subList(0, Math.min(my.size(), 10)));
                }
            });
        }
        pool2.shutdown();
        pool2.awaitTermination(10000, TimeUnit.MILLISECONDS);
        List<Runnable> last2 = pool2.shutdownNow();
        System.out.println("last runnable " + last);
        System.out.println(" cost all 2 " + (System.currentTimeMillis() - startTime));

    }

    public static void main(String[] args) throws Exception {
        new AgLockSync();

    }


}
