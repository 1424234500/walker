package com.walker.demo;

import java.util.concurrent.*;

/**
 * FutureTaskThread 中断测试 保留最新计算进展
 */
public class FutureTaskThread<V> {
    Thread thread;
    FutureTask<V> futureTask;


    public FutureTaskThread() {


    }

    public static void main(String[] args) throws Exception {
        test(3, 2, 2);      //3秒后 已经完成了任务 无需中断 立即获取完整结果
        test(3, 2, 8);     //3秒后 还没完成任务 中断 等待2秒 获取部分结果 3 -> 3+2
        test(3, 2, 5);      //3秒后 还没完成任务 中断 等待2秒 3 -> 3+2

//        test(3, 12);

    }

    private static void test(long sleep, long waitSignal, long should) throws Exception {
        FutureTaskThread<Integer> futureTaskThread = new FutureTaskThread<Integer>();
        futureTaskThread.setCallable(new Callable<Integer>() {
            int count = 0;

            @Override
            public Integer call() {
                while (true) {
                    if (!Thread.currentThread().isInterrupted() && count < should) {
                        System.out.println(Thread.currentThread().getName() + " " + count++);
                        try {
                            //模拟耗时操作
                            Thread.sleep(1000);
                        } catch (Exception e) { //Thread.interrupt会触发这个
                            System.out.println(Thread.currentThread().getName() + " isInterrupted res=" + count + " isInterrupted=" + Thread.currentThread().isInterrupted() + " complate " + (count >= should) + " by sleep ");
                            //中不中断由自己决定，如果需要真真中断线程，则需要重新设置中断位，如果不需要，则不用调用
                            Thread.currentThread().interrupt();
//                            break;
                        }
                    } else {
                        System.out.println(Thread.currentThread().getName() + " isInterrupted res=" + count + " isInterrupted=" + Thread.currentThread().isInterrupted() + " complate " + (count >= should));
                        break;
                    }
                }
                return count;
            }
        });
//        Thread.sleep(sleep);
        try {
            Object res = futureTaskThread.get(sleep, waitSignal, TimeUnit.SECONDS);
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FutureTaskThread<V> setCallable(Callable<V> callable) {
        futureTask = new FutureTask<V>(callable);
        thread = new Thread(futureTask);
        thread.start();
        return this;
    }

    /**
     * 等待结果 异常则抛出
     *
     * @param waitFuture 等待future任务执行一段时间
     * @param waitSigal  主动中断任务后 等待中断信号同步数据
     * @return 中断或执行完毕后 的 最新结果
     */
    public V get(long waitFuture, long waitSigal, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        V res;
        try {
            res = futureTask.get(waitFuture, unit); //倒计时等待 并无中断信号!
        } catch (TimeoutException e) { //等待超时
            thread.interrupt(); //手动关闭任务 发出线程中断信号 java ui情况下无法信息通知到位?????
//        发出信号2s后依然超时??
            res = futureTask.get(waitSigal, unit); //中断信号已发出 再等一小会儿获取结果 不然异常
        }
        return res;
    }

}
