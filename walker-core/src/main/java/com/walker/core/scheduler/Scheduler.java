package com.walker.core.scheduler;


import java.util.List;

/**
 * 定时器任务调度工具
 * <p>
 * 初始化任务 db cache
 * <p>
 * 加入任务
 * 移除任务
 * 修改任务
 * <p>
 * 策略 异常上抛 用者处理
 */
public interface Scheduler {

    /**
     * 启动
     */
    void start() throws Exception;

    /**
     * 暂停
     */
    void pause() throws Exception;


    /**
     * 关闭
     */
    void shutdown() throws Exception;

    /**
     * 添加任务
     */
    Task add(Task task) throws Exception;

    /**
     * 立即执行任务
     */
    Task run(Task task) throws Exception;


    /**
     * 移除任务
     */
    Task remove(Task task) throws Exception;

    /**
     * 保存任务  添加删除多个触发器
     */
    Task save(Task task) throws Exception;


    Task saveTrigger(Task task, List<String> cronOn, List<String> cronOff) throws Exception;


}
