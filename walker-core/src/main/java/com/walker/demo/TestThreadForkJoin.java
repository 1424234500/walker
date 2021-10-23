package com.walker.demo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * 测试多线程 fork join多任务协作
 */
public class TestThreadForkJoin {


    TestThreadForkJoin() throws InterruptedException, ExecutionException {
        // 创建包含Runtime.getRuntime().availableProcessors()返回值作为个数的并行线程的ForkJoinPool
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        // 提交可分解的PrintTask任务
        Future<Integer> future = forkJoinPool.submit(new RecursiveTask<Integer>() {
            @Override
            protected Integer compute() {
                RecursiveTask<Integer> sub1 = new RecursiveTask<Integer>() {
                    @Override
                    protected Integer compute() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return 1;
                    }
                };
                RecursiveTask<Integer> sub2 = new RecursiveTask<Integer>() {
                    @Override
                    protected Integer compute() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return 10;
                    }
                };
                sub1.fork();
                sub2.fork();

                return 100 + sub1.join() + sub2.join();
            }
        });
        System.out.println("计算出来的总和=" + future.get());
        // 关闭线程池
        forkJoinPool.shutdown();

    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        new TestThreadForkJoin();

    }

}
