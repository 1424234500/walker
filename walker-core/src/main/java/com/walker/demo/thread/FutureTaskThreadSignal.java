package com.walker.demo.thread;

import java.util.concurrent.*;

/**
 * FutureTaskThread 中断测试 保留最新计算进展
 * <p>
 * 自定义信号量
 */
public class FutureTaskThreadSignal<V> {
    Thread thread;
    FutureTask futureTask;

    volatile CountDownLatch signal = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        test(3000, 2, 2); //3秒后 已经完成了任务 无需中断 立即获取完整结果
        test(3000, 2, 8); //3秒后 还没完成任务 中断 等待2秒 获取部分结果
        test(3000, 4, 5); //3秒后 还没完成任务 中断 等待4秒 由于立即中断 依然获取部分结果

//        test(3, 12);

    }

    private static void test(long sleep, long waitSignal, long should) throws Exception {
        FutureTaskThreadSignal<Integer> futureTaskThread = new FutureTaskThreadSignal<Integer>();
        futureTaskThread.setCallable(new Callable<Integer>() {
            int count = 0;

            @Override
            public Integer call() throws Exception {
                while (true) {
                    if (!futureTaskThread.isInterrupted() && count < should) {
                        System.out.println(Thread.currentThread().getName() + " " + count++);
                        try {
                            //模拟耗时操作
                            Thread.sleep(1000);
                        } catch (Exception e) { //signal机制不会触发这个
                            System.out.println(Thread.currentThread().getName() + " isInterrupted sleep res=" + count + " isInterrupted=" + futureTaskThread.isInterrupted() + " complate " + (count >= should));
                        }
                    } else {
                        System.out.println(Thread.currentThread().getName() + " isInterrupted res=" + count + " isInterrupted=" + futureTaskThread.isInterrupted() + " complate " + (count >= should));
                        break;
                    }
                }
                return count;
            }
        });
        Thread.sleep(sleep);
        try {
            Object res = futureTaskThread.get(waitSignal, TimeUnit.SECONDS);
            System.out.println(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isInterrupted() {
        return signal.getCount() <= 0;
    }

    public FutureTaskThreadSignal<V> setCallable(Callable<V> callable) {
        futureTask = new FutureTask<V>(callable);
        thread = new Thread(futureTask);
        thread.start();
        return this;
    }

    /**
     * 等待结果 异常则抛出
     *
     * @param waitSigal 主动中断任务后 等待中断信号同步数据
     * @param unit
     * @return 中断或执行完毕后 的 最新结果
     */
    public V get(long waitSigal, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        V res;
        signal.countDown(); //手动关闭任务 发出线程中断信号
        signal.await();
        res = (V) futureTask.get(waitSigal, unit); //中断信号已发出 再等一小会儿获取结果 不然异常
        return res;
    }


}
