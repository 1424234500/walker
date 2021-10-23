package com.walker.demo;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁案例
 */
public class AgLockLockReadWrite {


    ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    int nThreads = 10;

    public static void main(String[] args) throws Exception {
        new AgLockLockReadWrite().test();

    }

    void test() throws Exception {
        for (int i = 0; i < 8; i++)
            new Thread() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {

                        Lock lock = reentrantReadWriteLock.readLock();
                        lock.lock();
                        try {
                            System.out.println(Thread.currentThread().getName() + " read file begin " + System.currentTimeMillis() + " rc " + reentrantReadWriteLock.getReadHoldCount() + " wc " + reentrantReadWriteLock.getWriteHoldCount());
                            try {
                                Thread.sleep((long) (Math.random() * 2000));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println(Thread.currentThread().getName() + " read file end " + System.currentTimeMillis());

                        } finally {
                            lock.unlock();
                        }
                        try {
                            Thread.sleep((long) (Math.random() * 100));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }.start();

        for (int i = 0; i < 2; i++)
            new Thread() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {

                        Lock lock = reentrantReadWriteLock.writeLock();
                        lock.lock();
                        try {
                            System.out.println(Thread.currentThread().getName() + " write file begin " + System.currentTimeMillis() + " rc " + reentrantReadWriteLock.getReadHoldCount() + " wc " + reentrantReadWriteLock.getWriteHoldCount());
                            try {
                                Thread.sleep((long) (Math.random() * 6000));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println(Thread.currentThread().getName() + " write file end " + System.currentTimeMillis());

                        } finally {
                            lock.unlock();
                        }
                        try {
                            Thread.sleep((long) (Math.random() * 1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }.start();

        try {
            Thread.sleep(1 * 20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


}
