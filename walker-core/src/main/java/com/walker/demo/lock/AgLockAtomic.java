package com.walker.demo.lock;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 数组遍历，四个线程协作处理
 */
public class AgLockAtomic {


    static AtomicInteger indexAllNow = new AtomicInteger(0);
    static Lock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    AgLockAtomic() throws Exception {
        int nThreads = 4;

//        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        ThreadPoolExecutor pool = new ThreadPoolExecutor(nThreads, nThreads, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1000)) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                super.beforeExecute(t, r);
                System.out.println(t.getName() + " before");
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                System.out.println(r.getClass() + " after");
            }
        };
        int size = 200;
        Integer[] arr = new Integer[size];
        for (int i = 0; i < size; i++) {
            arr[i] = i;
        }
        pool.execute(new AgLockThread<Integer>(arr, new Action<Integer>() {
            @Override
            public boolean action(Integer item) {
                boolean bool = indexAllNow.get() % 2 == 0;
                if (bool) {
                    System.out.println(Thread.currentThread().getName() + " action A " + item);
                }
                return bool;
            }
        }));
        pool.execute(new AgLockThread<Integer>(arr, new Action<Integer>() {
            @Override
            public boolean action(Integer item) {
                boolean bool = indexAllNow.get() % 3 == 0;
                if (bool) {
                    System.out.println(Thread.currentThread().getName() + " action B " + item);
                }
                return bool;
            }
        }));
        pool.execute(new AgLockThread<Integer>(arr, new Action<Integer>() {
            @Override
            public boolean action(Integer item) {
                boolean bool = indexAllNow.get() % 3 != 0 && indexAllNow.get() % 2 != 0;
                if (bool) {
                    System.out.println(Thread.currentThread().getName() + " action C " + item);
                }
                return bool;
            }
        }));

        pool.shutdown();
        pool.awaitTermination(100000, TimeUnit.MILLISECONDS);
        List<Runnable> last = pool.shutdownNow();
        System.out.println("last runnable " + last);

    }

    public static void main(String[] args) throws Exception {
        new AgLockAtomic();

    }

    interface Action<T> {
        boolean action(T item);
    }

    class AgLockThread<T> implements Runnable {

        Action<T> action;
        T[] arr;

        public AgLockThread(T[] arr, Action<T> action) {
            this.action = action;
            this.arr = arr;
        }

        @Override
        public void run() {
            while (indexAllNow.get() < this.arr.length) {
                lock.lock();
                try {
                    if (action.action(arr[indexAllNow.get()])) {
                        indexAllNow.addAndGet(1);
                        condition.signalAll();
                    } else {
                        try {
                            condition.await(1000, TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }


}
