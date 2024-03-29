package com.walker.core.util;

import com.walker.core.aop.Fun;
import com.walker.core.cache.ConfigMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;


/**
 * 线程工具
 * 管理四种线程池
 */
public class ThreadUtil {
    private static final Logger log = LoggerFactory.getLogger("Thread");

    /**
     * ThreadPoolUtils构造函数
     *
     * @param type 线程池类型
     *             //	 * @param corePoolSize 只对Fixed和Scheduled线程池起效
     */
    public static ExecutorService getExecutorServiceInstance(Type type) {
        return getExecutorServiceInstance(type, ConfigMgr.getInstance().get("corePoolSize", 5));
    }

    /**
     * 固定数量线程池
     *
     * @param corePoolSize
     */
    public static ExecutorService getExecutorServiceInstance(int corePoolSize) {
        return Executors.newFixedThreadPool(corePoolSize);
    }

    public static ExecutorService getExecutorServiceInstance(Type type, final int corePoolSize) {
        ExecutorService res;
        switch (type) {
            case SingleThread:
                // 构造一个只支持一个线程的线程池,相当于newFixedThreadPool(1) 队列
                // ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>())
                res = Executors.newSingleThreadExecutor();
                break;
            case CachedThread:
                // 构造一个缓冲功能的线程池 缓冲执行 不能当作 队列
                // ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
                res = Executors.newCachedThreadPool();
                break;
            case ScheduledThread:
                // 定时任务线程池
                // ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
                res = Executors.newScheduledThreadPool(corePoolSize);
                break;
            case FixedThread:
            default:
                // 构造一个固定线程数目的线程池
                // ThreadPoolExecutor(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
                res = Executors.newFixedThreadPool(corePoolSize);
        }
        return res;
    }

    /**
     * 添加一个线程任务  type三种线程池
     *
     * @param type     ThreadHelp.
     * @param runnable 命令
     */
    public static void execute(Type type, Runnable runnable) {
        ExecutorService exec = getExecutorServiceInstance(type);

        exec.execute(runnable);
    }

    public static void execute(Runnable runnable) {
        execute(Type.DefaultThread, runnable);
    }

    /**
     * 添加多个线程任务 type三种线程池
     *
     * @param type      ThreadHelp.
     * @param runnables 命令
     */
    public static void execute(Type type, List<Runnable> runnables) {
        ExecutorService exec = getExecutorServiceInstance(type);
        for (Runnable runable : runnables) {
            exec.execute(runable);
        }
    }

    /**
     * 待以前提交的任务执行完毕后关闭线程池
     * <p>启动一次顺序关闭，执行以前提交的任务，但不接受新任务。
     * 如果已经关闭，则调用没有作用。</p>
     *
     * @param type ThreadHelp.
     */
    public static void shutDown(Type type) {
        ExecutorService exec = getExecutorServiceInstance(type);
        exec.shutdown();
    }

    /**
     * 试图停止所有正在执行的活动任务
     * <p>试图停止所有正在执行的活动任务，暂停处理正在等待的任务，并返回等待执行的任务列表。</p>
     * <p>无法保证能够停止正在处理的活动执行任务，但是会尽力尝试。</p>
     *
     * @param type ThreadHelp.
     * @return 等待执行的任务的列表
     */
    public static List<Runnable> shutDownNow(Type type) {
        ExecutorService exec = getExecutorServiceInstance(type);
        return exec.shutdownNow();
    }

    /**
     * 判断线程池是否已关闭
     *
     * @param type ThreadHelp.
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isShutDown(Type type) {
        ExecutorService exec = getExecutorServiceInstance(type);
        return exec.isShutdown();
    }

    /**
     * 关闭线程池后判断所有任务是否都已完成
     * <p>注意，除非首先调用 shutdown 或 shutdownNow，否则 isTerminated 永不为 true。</p>
     *
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isTerminated(Type type) {
        ExecutorService exec = getExecutorServiceInstance(type);
        return exec.isTerminated();
    }

    /**
     * 请求关闭、发生超时或者当前线程中断
     * <p>无论哪一个首先发生之后，都将导致阻塞，直到所有任务完成执行。</p>
     *
     * @param timeout 最长等待时间
     * @param unit    时间单位
     * @return {@code true}: 请求成功<br>{@code false}: 请求超时
     * @throws InterruptedException 终端异常
     */
    public static boolean awaitTermination(Type type, long timeout, TimeUnit unit) {
        ExecutorService exec = getExecutorServiceInstance(type);
        boolean res = false;
        try {
            res = exec.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 提交一个Callable任务用于执行
     * <p>如果想立即阻塞任务的等待，则可以使用{@code result = exec.submit(aCallable).get();}形式的构造。</p>
     *
     * @param task 任务
     * @param <T>  泛型
     * @return 表示任务等待完成的Future, 该Future的{@code get}方法在成功完成时将会返回该任务的结果。
     */
    public static <T> Future<T> submit(Type type, Callable<T> task) {
        ExecutorService exec = getExecutorServiceInstance(type);
        Future<T> future = exec.submit(task);
//		future.cancel(false);
//	    future.get();
        return future;
    }

    /**
     * 提交一个Runnable任务用于执行
     *
     * @param task   任务
     * @param result 返回的结果
     * @param <T>    泛型
     * @return 表示任务等待完成的Future, 该Future的{@code get}方法在成功完成时将会返回该任务的结果。
     */
    public static <T> Future<T> submit(Type type, Runnable task, final T result) {
        ExecutorService exec = getExecutorServiceInstance(type);
        return exec.submit(task, result);
    }

    /**
     * 提交一个Runnable任务用于执行
     *
     * @param task 任务
     * @return 表示任务等待完成的Future, 该Future的{@code get}方法在成功完成时将会返回null结果。
     */
    public static Future<?> submit(Type type, Runnable task) {
        ExecutorService exec = getExecutorServiceInstance(type);
        return exec.submit(task);
    }

    /**
     * 执行给定的任务
     * <p>当所有任务完成时，返回保持任务状态和结果的Future列表。
     * 返回列表的所有元素的{@link Future#isDone}为{@code true}。
     * 注意，可以正常地或通过抛出异常来终止已完成任务。
     * 如果正在进行此操作时修改了给定的 collection，则此方法的结果是不确定的。</p>
     *
     * @param tasks 任务集合
     * @param <T>   泛型
     * @return 表示任务的 Future 列表，列表顺序与给定任务列表的迭代器所生成的顺序相同，每个任务都已完成。
     * @throws InterruptedException 如果等待时发生中断，在这种情况下取消尚未完成的任务。
     */
    public static <T> List<Future<T>> invokeAll(Type type, Collection<? extends Callable<T>> tasks) {
        ExecutorService exec = getExecutorServiceInstance(type);
        try {
            return exec.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行给定的任务
     * <p>当所有任务完成或超时期满时(无论哪个首先发生)，返回保持任务状态和结果的Future列表。
     * 返回列表的所有元素的{@link Future#isDone}为{@code true}。
     * 一旦返回后，即取消尚未完成的任务。
     * 注意，可以正常地或通过抛出异常来终止已完成任务。
     * 如果此操作正在进行时修改了给定的 collection，则此方法的结果是不确定的。</p>
     *
     * @param tasks   任务集合
     * @param timeout 最长等待时间
     * @param unit    时间单位
     * @param <T>     泛型
     * @return 表示任务的 Future 列表，列表顺序与给定任务列表的迭代器所生成的顺序相同。如果操作未超时，则已完成所有任务。如果确实超时了，则某些任务尚未完成。
     * @throws InterruptedException 如果等待时发生中断，在这种情况下取消尚未完成的任务
     */
    public static <T> List<Future<T>> invokeAll(Type type, Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
        ExecutorService exec = getExecutorServiceInstance(type);
        try {
            return exec.invokeAll(tasks, timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行给定的任务
     * <p>如果某个任务已成功完成（也就是未抛出异常），则返回其结果。
     * 一旦正常或异常返回后，则取消尚未完成的任务。
     * 如果此操作正在进行时修改了给定的collection，则此方法的结果是不确定的。</p>
     *
     * @param tasks 任务集合
     * @param <T>   泛型
     * @return 某个任务返回的结果
     * @throws InterruptedException 如果等待时发生中断
     * @throws ExecutionException   如果没有任务成功完成
     */
    public static <T> T invokeAny(Type type, Collection<? extends Callable<T>> tasks) {
        ExecutorService exec = getExecutorServiceInstance(type);
        try {
            return exec.invokeAny(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行给定的任务
     * <p>如果在给定的超时期满前某个任务已成功完成（也就是未抛出异常），则返回其结果。
     * 一旦正常或异常返回后，则取消尚未完成的任务。
     * 如果此操作正在进行时修改了给定的collection，则此方法的结果是不确定的。</p>
     *
     * @param tasks   任务集合
     * @param timeout 最长等待时间
     * @param unit    时间单位
     * @param <T>     泛型
     * @return 某个任务返回的结果
     * @throws InterruptedException 如果等待时发生中断
     * @throws ExecutionException   如果没有任务成功完成
     * @throws TimeoutException     如果在所有任务成功完成之前给定的超时期满
     */
    public static <T> T invokeAny(Type type, Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
        ExecutorService exec = getExecutorServiceInstance(type);
        try {
            return exec.invokeAny(tasks, timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 延迟执行Runnable命令
     *
     * @param runnable 命令
     * @param delay    延迟时间
     * @param unit     单位
     * @return 表示挂起任务完成的ScheduledFuture，并且其{@code get()}方法在完成后将返回{@code null}
     */
    public static ScheduledFuture<?> schedule(final Runnable runnable, final long delay, final TimeUnit unit) {
        ExecutorService scheduleExec = getExecutorServiceInstance(Type.ScheduledThread);
        return ((ScheduledExecutorService) scheduleExec).schedule(runnable, delay, unit);
    }

    /**
     * 延迟执行Callable命令
     *
     * @param callable 命令
     * @param delay    延迟时间
     * @param unit     时间单位
     * @param <V>      泛型
     * @return 可用于提取结果或取消的ScheduledFuture
     */
    public static <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        ExecutorService scheduleExec = getExecutorServiceInstance(Type.ScheduledThread);
        return ((ScheduledExecutorService) scheduleExec).schedule(callable, delay, unit);
    }

    /**
     * 延迟并循环执行命令
     *
     * @param runnable     命令
     * @param initialDelay 首次执行的延迟时间
     * @param period       连续执行之间的周期
     * @param unit         时间单位
     * @return 表示挂起任务完成的ScheduledFuture，并且其{@code get()}方法在取消后将抛出异常
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        ExecutorService scheduleExec = getExecutorServiceInstance(Type.ScheduledThread);
        return ((ScheduledExecutorService) scheduleExec).scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    /**
     * 延迟并以固定休息时间循环执行命令
     *
     * @param runnable     命令
     * @param initialDelay 首次执行的延迟时间
     * @param delay        每一次执行终止和下一次执行开始之间的延迟
     * @param unit         时间单位
     * @return 表示挂起任务完成的ScheduledFuture，并且其{@code get()}方法在取消后将抛出异常
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        ExecutorService scheduleExec = getExecutorServiceInstance(Type.ScheduledThread);
        return ((ScheduledExecutorService) scheduleExec).scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }

    /**
     * 线程工具 耗时处理
     *
     * @param fun
     * @return 回调线程id
     */
    public static Thread thread(final Fun<Long> fun) {
        Thread thread = new Thread() {
            public void run() {
                if (fun != null) {
                    fun.make(this.getId());
                }
            }
        };
        thread.start();
        return thread;
    }

    public static void sleep(long time) {
        try {
            //Thread.sleep(time);
            TimeUnit.MILLISECONDS.sleep(time);
        } catch (InterruptedException e) {
            //e.printStackTrace();
            System.out.println("Thread.sleep Interrupted " + time);
        }
    }

    public enum Type {

        /**
         * 固定数量的线程池 构造一个固定线程数目的线程池
         */
        DefaultThread,

        /**
         * 固定数量的线程池 构造一个固定线程数目的线程池
         */
        FixedThread,
        /**
         * 缓存? 构造一个缓冲功能的线程池
         */
        CachedThread,
        /**
         * 单线程池 队列 只支持一个线程的线程池
         */
        SingleThread,
        /**
         * 定时任务线程池
         */
        ScheduledThread,

        /**
         * 延迟队列 线程池
         */
        DelayThread,

    }


}