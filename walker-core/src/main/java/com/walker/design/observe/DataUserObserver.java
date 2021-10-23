package com.walker.design.observe;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;


/**
 * 发布订阅 观察者模式       事件机制 反向引用
 * <p>
 * 气象站观测数据问题
 * <p>
 * 数据中心
 * 利用java.util.Observable替我们实现 数据结构管理（vector 安全list） 添加 删除 通知
 * <p>
 * <p>
 * 用户/设备 订阅专项数据
 * 利用Observer接口实现订阅接口
 * <p>
 * <p>
 * <p>
 * main
 * 初始化数据中心 初始化用户 绑定订阅关系
 * <p>
 * 等待
 * 用户/设备 被（推送）（是否 线程隔离） 被通知数据变化
 */
public class DataUserObserver implements Observer {
    private static final Logger log = LoggerFactory.getLogger(DataUserObserver.class);

    private String name;

    public String getName() {
        return name;
    }

    public DataUserObserver setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 多观察者 多中心问题
     * 同接口去注册来多个订阅中心
     *
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof DataCenterObservable) {
            log.info("observer " + name + " get " + arg);
        } else {
            log.warn("no implement Observable ?? ");
        }


    }


}



