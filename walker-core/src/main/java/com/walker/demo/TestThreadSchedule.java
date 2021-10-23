package com.walker.demo;

import com.walker.util.TimeUtil;
import lombok.SneakyThrows;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestThreadSchedule {
    static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] argv) throws InterruptedException {

        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName() + " now " + count + " at " + TimeUtil.getTimeYmdHms() + " 固定周期 每秒打印时刻 ");
            }
        }, 1, 1, TimeUnit.SECONDS);

        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " now " + count + " at " + TimeUtil.getTimeYmdHms() + " begin ======= 固定周期 实例耗时超过周期时 不会并发(api介绍) 周期时刻顺延到上个任务实例结束后 立即执行下一个实例");
                Thread.sleep(4000);
                System.out.println(Thread.currentThread().getName() + " now " + count + " at " + TimeUtil.getTimeYmdHms() + " end   ======= 固定周期 实例耗时超过周期时 不会并发(api介绍) 周期时刻顺延到上个任务实例结束后 立即执行下一个实例");
                count.addAndGet(1);
            }
        }, 1, 2, TimeUnit.SECONDS);

        scheduledThreadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " now " + count + " at " + TimeUtil.getTimeYmdHms() + " begin >>> 固定周期 实例耗时低于周期时 周期时刻立即执行下一个实例");
                Thread.sleep(500);
                System.out.println(Thread.currentThread().getName() + " now " + count + " at " + TimeUtil.getTimeYmdHms() + " end >>>>>>固定周期 实例耗时低于周期时 周期时刻立即执行下一个实例");
                count.addAndGet(1);
            }
        }, 1, 2, TimeUnit.SECONDS);

        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + " now " + count + " at " + TimeUtil.getTimeYmdHms() + " begin ###### 固定执行间隔 周期 = 间隔 + 上个任务实例耗时");
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + " now " + count + " at " + TimeUtil.getTimeYmdHms() + " end   ###### 固定执行间隔 周期 = 间隔 + 上个任务实例耗时");
                count.addAndGet(1);
            }
        }, 1, 2, TimeUnit.SECONDS);


        Thread.sleep(20000);
        //发送中断信号?
        scheduledThreadPoolExecutor.shutdown();
        //等待
        if (!scheduledThreadPoolExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
            List<Runnable> last = scheduledThreadPoolExecutor.shutdownNow();
            System.out.println("last runnable " + last);
        }

    }


}
