package com.walker.design.proxy;

/**
 * 代理模式     为另一个对象提供一个替身或者占位符来访问这个对象
 * 类似于装饰者但目的不同
 * <p>
 * 用户自己能更改设置，但不能给自己加分的问题
 * <p>
 * 代理动物园：
 * 远程代理rmi     虚拟代理    保护代理
 * 防火墙代理 firewall Proxy
 * 智能引用代理  Smart Reference Proxy 当主题被引用时 额外计数
 * 缓存代理    Caching Proxy
 * 同步代理    Synchronization Proxy   多线程安全访问机制
 * 外观代理    复杂隐藏代理  Complexity Hiding Proxy 提供代理控制访问
 * 写入时复制代理 Copy-On-Write Proxy 控制对象的复制 延迟对象的复制 CopyOnWriteArrayList
 */
public interface UserMapper {

    String getName();

    void setName(String name);

    int getScore();

    void addScore();


    String hello();
}
