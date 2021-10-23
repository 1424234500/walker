package com.walker.design.observe;


/**
 * 发布订阅 观察者模式       事件机制 反向引用
 * <p>
 * <p>
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
public class Test {

    public static void main(String[] argv) {

        DataCenterObservable<String> dataCenterObservable = new DataCenterObservable<>();
        DataUserObserver dataUserObserver1 = new DataUserObserver().setName("n1");
        DataUserObserver dataUserObserver2 = new DataUserObserver().setName("n2");
        dataCenterObservable.addObserver(dataUserObserver1);
        dataCenterObservable.addObserver(dataUserObserver2);

        dataCenterObservable.sendData("helloall");

        dataCenterObservable.deleteObserver(dataUserObserver2);

        dataCenterObservable.sendData("hello1");


    }


}



