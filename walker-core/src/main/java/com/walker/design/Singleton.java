package com.walker.design;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * 单例模式
 * <p>
 * 巧克力工厂问题     顺序单实现
 * <p>
 * <p>
 * Singleton.getInstance()
 * <p>
 * 是否需要迟加载?
 * 是否线程安全?
 */
public class Singleton {
    /**
     * volatile确保多线程可见性
     */
    volatile static Singleton instance;
    /**
     * 1.简单方式
     * 非迟加载
     * 安全 jvm保证加载类时必定初始化static且只有一次
     */
    static Singleton instance1 = new Singleton();

    /**
     * 私有构造器
     */
    private Singleton() {
    }

    public static void main(String[] argvs) {
        Singleton.getInstance();
        Singleton.getInstanceSy();
        Singleton.getInstanceSynMethod();

    }

    /**
     * 2.锁方法
     * 迟加载
     * 安全
     * 性能问题
     */
    public static synchronized Singleton getInstanceSynMethod() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    /**
     * 3.锁 双重检查
     * 迟加载
     * 安全
     * violate可见性注意
     */
    public static Singleton getInstanceSy() {
        if (instance == null) {
            synchronized (instance) {
                if (instance == null) {
                    instance = new Singleton();
                    //隐患 在Java指令中创建对象和赋值操作是分开进行的，
                    // 就是说instance = new Singleton();
                    // 语句是分两步执行的。但是JVM并不保证这两个操作的先后顺序，
                    // 就是说有可能JVM会为新的Singleton实例分配空间，然后直接赋值给instance成员
                }
            }
        }
        return instance;
    }

    /**
     * 内部类模式 可靠
     */
    public static Singleton getInstance() {
        return SingletonFactory.instance;
    }

    /**
     * 私有静态内部类
     */
    private static class SingletonFactory {
        private static final Singleton instance;
        private static final AtomicInteger count = new AtomicInteger(0);

        static {
            System.out.println("静态内部类初始化" + SingletonFactory.class + " count:" + count.addAndGet(1));
            instance = new Singleton();
        }
    }

}
