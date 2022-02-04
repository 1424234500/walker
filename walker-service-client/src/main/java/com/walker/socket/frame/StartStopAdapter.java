package com.walker.socket.frame;

import com.walker.demo.FutureTaskThread;
import com.walker.system.Pc;
import com.walker.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 使用future模板控制socket事件启停
 *
 * 监听进程信号 优雅关闭
 *
 */

public abstract class StartStopAdapter implements StartStop {
    public Logger log = LoggerFactory.getLogger(getClass());

    volatile FutureTaskThread<String> futureTaskThread;

    public StartStopAdapter(){
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.warn("---ShutdownHook receive kill signal ----------" );
                String nowTime = TimeUtil.getTimeYmdHms();
                String str = Pc.getRuntime();
                log.warn("---"
                        + " who " + this.toString()
                        + " nowTime " + nowTime
                        + " " + str
                );

                log.warn("---ShutdownHook success kill signal----------");
            }
        });
    }

    /**
     * 开启监听线程
     */
    @Override
    synchronized public void start() throws Exception {
        futureTaskThread = new FutureTaskThread<String>();
        futureTaskThread.setCallable((Callable) () -> {
            log.info("begin 启动 " + Thread.currentThread().getName() + " " + this );

//         中断后退出循环 意味着关闭server
//          内部一定需要 线程循环 等待中断信号 退出
            eventLoopKeeper();    //ui线程 隔离? 导致当前线程不一致 无法接收到中断信号?
            log.info("break 退出 " + Thread.currentThread().getName() + " " + this);
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

    /**
     * reactor ?
     * @throws Exception
     */
    public abstract void eventLoopKeeper() throws Exception;

    /**
     * 中断后台线程 等待关闭超时 主动stop ui
     * 避免同线程?重入锁?
     */
    @Override
    synchronized public void stop(long timemillWait) throws Exception {
        if (futureTaskThread != null) {
            log.warn("开始关闭 " + Thread.currentThread().getName() + " " + this );

            Object res = null;
            try {
                res = futureTaskThread.get(timemillWait, 1500, TimeUnit.MILLISECONDS);
                log.warn("关闭成功 " + Thread.currentThread().getName() + " " + this + " " + res);
                futureTaskThread = null;
            } catch (Exception e) {
                log.warn("关闭异常 " + Thread.currentThread().getName() + " " + this + " " + res + " " + e.getMessage());
                throw e;
            }
        }
    }

    @Override
    public boolean isStart() {
        return futureTaskThread != null;
    }

}
