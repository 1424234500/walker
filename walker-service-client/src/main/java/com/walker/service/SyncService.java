package com.walker.service;


import com.walker.mode.Bean;

/**
 * 调度器
 *
 * 周期执行任务
 *      sql操作
 *
 * 初始化数据
 *
 * 造数
 *
 * 同步数据
 *
 *
 *
 */
public interface SyncService {
    /**
     * 初始化周期基本数据
     * @param args
     */
    Bean doBaseData(Bean args);

    /**
     * 周期执行sql列表
     */
    Bean doAction(Bean args);




    /**
     * 高耗时 初始化地理信息 异步
     * @param args
     */
    Bean syncArea(Bean args);


    /**
     * 高耗时 初始化信息 异步
     * @param args
     */
    Bean syncDept(Bean args);

    /**
     * 高耗时 初始化信息 异步
     * @param args
     */
    Bean syncUser(Bean args);


    /**
     * 高耗时 初始化信息 异步
     * @param args
     */
    Bean makeUser(Bean args);


    /**
     * 高耗时 初始化信息 异步
     * @param args
     */
    Bean makeDept(Bean args);


    /**
     * 高耗时 初始化信息 异步
     * @param args
     */
    Bean makeRole(Bean args);




}
