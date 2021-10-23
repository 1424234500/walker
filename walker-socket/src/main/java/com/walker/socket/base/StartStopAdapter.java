package com.walker.socket.base;

import com.walker.demo.FutureTaskThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 使用future模板控制socket事件启停
 * <p>
 * 底层实现详情
 */

public abstract class StartStopAdapter implements StartStop {
    public Logger log = LoggerFactory.getLogger(getClass());

    volatile FutureTaskThread<String> futureTaskThread;

    /**
     * 开启监听线程
     */
    @Override
    synchronized public void start() throws Exception {
        futureTaskThread = new FutureTaskThread<String>();
        futureTaskThread.setCallable((Callable) () -> {
            log.info(Thread.currentThread().getName() + " " + this + " 开始启动");

//         中断后退出循环 意味着关闭server
            eventLoopKeeper();    //ui线程 隔离? 导致当前线程不一致 无法接收到中断信号?
            log.info(Thread.currentThread().getName() + " " + this + " 退出loop端口监听 自动关闭");
//            stop(3000); //自己异常后需主动清理stop ? 有这种渠道吗? 重复不可锁退出触发异常???
            return Thread.currentThread().getName();
        });
//        自动停止设置
//        new Thread(){
//            public void run(){
//                try {
//                    Thread.sleep(6000);
//                    StartStopAdapter.this.stop(4000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
    }

    public abstract void eventLoopKeeper() throws Exception;

    /**
     * 中断后台线程 等待关闭超时 主动stop ui
     * 避免同线程?重入锁?
     */
    @Override
    synchronized public void stop(long timemillWait) throws Exception {
        if (futureTaskThread != null) {
            log.warn(Thread.currentThread().getName() + " " + this + " 开始关闭");

            Object res = null;
            try {
                res = futureTaskThread.get(timemillWait, 3000, TimeUnit.MILLISECONDS);
                log.warn(Thread.currentThread().getName() + " " + this + " 关闭成功 " + res);
                futureTaskThread = null;
            } catch (Exception e) {
                log.warn(Thread.currentThread().getName() + " " + this + " 关闭异常 " + res + " " + e.getMessage());
                throw e;
            }
        }
    }

    @Override
    public boolean isStart() {
        return futureTaskThread != null;
    }

}
