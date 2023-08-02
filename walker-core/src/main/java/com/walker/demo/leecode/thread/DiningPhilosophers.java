package com.walker.demo.leecode.thread;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

//哲学家从 0 到 4 按 顺时针 编号。请实现函数 void wantsToEat(philosopher, pickLeftFork, pickRightFork, eat, putLeftFork, putRightFork)：
//
//philosopher 哲学家的编号。
//pickLeftFork 和 pickRightFork 表示拿起左边或右边的叉子。
//eat 表示吃面。
//putLeftFork 和 putRightFork 表示放下左边或右边的叉子。
//由于哲学家不是在吃面就是在想着啥时候吃面，所以思考这个方法没有对应的回调。
//给你 5 个线程，每个都代表一个哲学家，请你使用类的同一个对象来模拟这个过程。在最后一次调用结束之前，可能会为同一个哲学家多次调用该函数。
class DiningPhilosophers {

    List<AtomicInteger> list = new ArrayList<>();

    public DiningPhilosophers() {
        for (int i = 0; i < 5; i++) {
            list.add(new AtomicInteger(0));
        }
    }

    // call the run() method of any runnable to execute its code
    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {
        boolean f = true;
        while (f) {
            AtomicInteger left = list.get(philosopher == 0 ? 4 : philosopher - 1);
            AtomicInteger right = list.get(philosopher == 4 ? 0 : philosopher + 1);
            if (left.compareAndSet(0, 1)) {
                if (right.compareAndSet(0, 1)) {
                    pickLeftFork.run();
                    pickRightFork.run();
                    eat.run();
                    putLeftFork.run();
                    putRightFork.run();
                    right.compareAndSet(1, 0);
                    f = false;
                }
                left.compareAndSet(1, 0);
            }
            Thread.sleep((long) (Math.random() * 100));
        }
    }
}