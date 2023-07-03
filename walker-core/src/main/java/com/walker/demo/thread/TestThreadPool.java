package com.walker.demo.thread;


/*8.ThreadPoolExecutor
        ThreadLocal  数据副本 线程切面耗时
        线程同步问题

        sleep：定时器唤醒	不释放锁
        wait： 需要在同步块里 notify随机唤醒/notifyAll唤醒所有
        park： 需要在同步块里 unpark唤醒指定
        Condition：signal随机唤醒/signalAll

        线程池构造 队列选择  队列容量 分工核心数评估
        拒绝策略s
        线程状态

        ExecutorService工厂制造 fixed固定数量 sing单线 sch单线周期(间隔时间or固定时间)
        ThreadPoolExecutor(int corePoolSize,		//核心数
        线程池个数=CPU的数量*CPU的使用率*（1+等待时间/计算时间）

        int maximumPoolSize,	//最大数
        long keepAliveTime,	//非核心存活时间
        TimeUnit unit,		//时间单位
        BlockingQueue<Runnable> workQueue,	//队列实现
        SynchronousQueue  eg:  4, 100 	    直接提交队列：没有容量，每一个插入操作都要等待一个相应的删除操作。通常使用需要将maximumPoolSize的值设置很大，否则很容易触发拒绝策略。
        ArrayBlockingQueue	有界的任务队列
        LinkedBlockingQueue	无界(可指定容量有界)的任务队列：线程个数最大为corePoolSize，如果任务过多，则不断扩充队列，直到内存资源耗尽。
        PriorityBlockingQueue	优先任务队列：是一个无界的特殊队列，可以控制任务执行的先后顺序，而上边几个都是先进先出的策略。
        DelayQueue 是 Delayed 元素的一个无界阻塞队列，只有在延迟期满时才能从中提取元素。该队列的头部 是延迟期满后保存时间最长的 Delayed 元素。如果延迟都还没有期满，则队列没有头部，并且 poll 将返回 null。
        ThreadFactory threadFactory,			//工厂覆盖
        RejectedExecutionHandler handler) 	//丢弃模式
        JDK提供的线程池拒绝策略
        策略名称	描述
        AbortPolicy	该策略会直接抛出异常，阻止系统正常 工作。线程池默认为此。
        CallerRunsPolicy	只要线程池未关闭，该策略直接在调用者线程中，运行当前被丢弃的任务。
        DiscardOledestPolicy	该策略将丢弃最老的一个请求，也就是即将被执行的一个任务，并尝试重新提交当前任务。
        DiscardPolicy	该策略默默地丢弃无法处理的任务，不予任务处理。


        添加任务corePoolSize -> workQueue -> maximumPoolSize -> handler
        优先顺序 核心线程corePoolSize、任务队列workQueue、最大线程maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务。
        但是 后续核心空余出来一个该消费队列?，非核心空余出来一个消费队列???

        线程池有四种状态，分别为RUNNING、SHURDOWN、STOP、TERMINATED。
        线程池创建后处于RUNNING状态。
        调用shutdown后处于SHUTDOWN状态,正常关闭，停止接收新的任务，继续执行已经提交的任务（包含提交正在执行和提交未执行）直到完毕后自动关闭！
        调用shutdownNow后处于STOP状态，发出interrupted以中止线程的运行。各个线程会抛出InterruptedException异常（前提是线程中运行了sleep、interrupted()等方法） 此方法会将任务队列中的任务移除并以列表形式返回

        如何实现线程池 所有任务执行完毕后关闭
        CountDownLatch并发计数器 实现超时等待	CountDownLatch.await(300,TimeUnit.SECONDS);

        es.shutdown();
        if(!es.awaitTermination(20,TimeUnit.SECONDS)){//20S
        System.out.println(" 到达指定时间，还有线程没执行完，不再等待，关闭线程池!");
        es.shutdownNow();
        }
        当线程池处于SHUTDOWN或STOP状态，并且所有工作线程已经销毁，任务缓存队列已经清空或执行结束后，线程池被设置为TERMINATED状态。*/
public class TestThreadPool {


}
