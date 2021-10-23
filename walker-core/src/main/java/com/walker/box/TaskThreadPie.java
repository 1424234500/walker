package com.walker.box;


import com.walker.util.Tools;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多线程协作统计工具抽象
 *
 * 我有多少个任务
 *
 * 你按配置开启10个线程协作完成任务
 * 你按配置控制队列最多允许排队200个
 * 你得让我实现每个任务 你得告诉我该实现第n个任务  要能迭代
 * 你得按配置定时5s展示一下进度信息
 * 你得在所有任务完成后 展示结果统计 执行了都少个任务 异常了多少个 耗时了多久
 *
 *
 *
 */
abstract class Pie{
    public abstract void onStartThread(int threadNo) throws Exception;
    public void onMoreAction(){
        Tools.out("no onMoreAction");
    }
    void onScheduleRun(){
        Tools.out(process());
    };
    void onAllThreadFinished(long costAll, int countAll, int countException){
        Tools.out(process() + " pie end!!! ");
    };
    abstract String process();
}
public abstract class TaskThreadPie extends Pie{
    /**
     * 是否打印更多日志
     */
    boolean isDetail = false;
    /**
     * 线程数
     */
    int threadSize = Runtime.getRuntime().availableProcessors();
    /**
     * 线程数
     */
    int threadSizeMax = threadSize;
    /**
     * 进度 打印周期ms
     */
    long sleepTimeSch = 5000;
    /**
     * 池任务超时等待时间
     */
    long timemillesWait = Integer.MAX_VALUE;

    public int getThreadSizeMax() {
        return threadSizeMax;
    }

    public TaskThreadPie setThreadSizeMax(int threadSizeMax) {
        this.threadSizeMax = threadSizeMax;
        return this;
    }

    public long getTimemillesWait() {
        return timemillesWait;
    }

    public TaskThreadPie setTimemillesWait(long timemillesWait) {
        this.timemillesWait = timemillesWait;
        return this;
    }

    long timeStart = System.currentTimeMillis();
    ThreadPoolExecutor pool;

    public ThreadPoolExecutor getPool() {
        return pool;
    }

    public TaskThreadPie setPool(ThreadPoolExecutor pool) {
        this.pool = pool;
        return this;
    }

    public ScheduledExecutorService getSch() {
        return sch;
    }

    public TaskThreadPie setSch(ScheduledExecutorService sch) {
        this.sch = sch;
        return this;
    }

    ScheduledExecutorService sch;

    AtomicInteger countNow; //当前完成数量
    AtomicInteger countException; //当前异常数量
    int countAll;   //总任务数量

    public boolean isDetail() {
        return isDetail;
    }

    public TaskThreadPie setDetail(boolean detail) {
        isDetail = detail;
        return this;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public TaskThreadPie setThreadSize(int threadSize) {
        this.threadSize = threadSize;
        this.threadSizeMax = this.threadSize;
        return this;
    }

    public long getSleepTimeSch() {
        return sleepTimeSch;
    }

    public TaskThreadPie setSleepTimeSch(long sleepTimeSch) {
        this.sleepTimeSch = sleepTimeSch;
        return this;
    }

    public int getCountAll() {
        return countAll;
    }

    public TaskThreadPie setCountAll(int countAll) {
        this.countAll = countAll;
        return this;
    }

    public TaskThreadPie(int countAll){
        this.countAll = countAll;
//        避免全部cpu占用
        if(this.threadSize > 1){
            this.threadSize -= 1;
        }
    }
    public void start() {
        Tools.out("start pie ", threadSize, countAll, sleepTimeSch, isDetail);
        if(countAll <= 0) return;
        countException = new AtomicInteger(0);
        countNow = new AtomicInteger(0);
//        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.threadSize){
        pool = new ThreadPoolExecutor(this.threadSize, this.threadSizeMax
                , 0L, TimeUnit.MILLISECONDS
                , new LinkedBlockingQueue<Runnable>(this.countAll)
                , new ThreadPoolExecutor.AbortPolicy()
        ){
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
//                System.out.println("准备执行线程：" + r.toString() +"==="  + t.getName());
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
//                System.out.println("执行完成线程：" + r.toString());
            }

            @Override
            protected void terminated() {
                Tools.out("thread pool terminated" );
            }

        };
        sch = Executors.newSingleThreadScheduledExecutor();
        sch.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                onScheduleRun();
            }
        }, 0, this.sleepTimeSch, TimeUnit.MILLISECONDS);
        int nowi = 0;
        //把目标任务都添加进入队列
        while (nowi < countAll) {
            int ccc = pool.getCorePoolSize();
            int acc = pool.getActiveCount();
            int qcc = pool.getQueue().size();
            if(qcc < ccc * 1000) {
                final int tno = nowi++;
                detail("init start thread", tno, acc, qcc, ccc);
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        long timeStart = System.currentTimeMillis();
                        try {
                            onStartThread(tno);
                        } catch (Exception e) {
                            countException.addAndGet(1);
                            e.printStackTrace();
                        } finally {
                            countNow.addAndGet(1);
                            long deta = System.currentTimeMillis() - timeStart;
                            detail("##thread finish " + tno + " " + Tools.calcTime(deta));
                        }
                    }
					@Override
					public String toString(){
						return "pie thread no:" + tno;
					}
                });
            }else{
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            onMoreAction();
        }catch (Exception e){
            e.printStackTrace();
        }
//        等待任务队列消费完毕
        pool.shutdown();
        try{
//            超时控制 并打印剩下的队列
			if(!pool.awaitTermination(timemillesWait, TimeUnit.MILLISECONDS)){
                Tools.out("===============================================");
                Tools.out("WARN await time out now shutdown now !! ", timemillesWait);
				List<Runnable> last = pool.shutdownNow();
				Tools.out("WARN shutdown now last queue:" + last.size());
				Tools.formatOut(last);
                Tools.out("===============================================");
			}
        }catch (Exception e){
            e.printStackTrace();
        }
		
        sch.shutdownNow();
        onAllThreadFinished(System.currentTimeMillis() - timeStart, countAll, countException.get());
    }

    @Override
    public String process() {
//        deta countNow = all countAll
        long deta = System.currentTimeMillis() - timeStart;
        long all = (long) (1f * deta * countAll / countNow.get());
        long last =  all - deta;

        int ccc = pool.getCorePoolSize();
        int acc = pool.getActiveCount();
        int qcc = pool.getQueue().size();

        return "Process[count " + countNow + "/" + countException + "/" + countAll
                + " queue " + acc + "/" + ccc + "/" + qcc
                + " qps " + (int)(countNow.get() * 1000f * 100f / deta) / 100f + "/s"
                + " percent " + (int)(countNow.get() * 1000f / countAll) / 10f + "% " +
                " cost " + Tools.calcTime(deta) + " last " + Tools.calcTime(last) + " all " + Tools.calcTime(all) + "]";
    }


    private void detail(Object...objects){
        if(this.isDetail)
			Tools.out(objects);
    }


}
