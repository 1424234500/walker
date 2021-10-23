package com.walker.core.mode;


import com.walker.util.TimeUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class CountCenter {
    /**
     * 私有构造器
     */
    private CountCenter(){}
    /**
     * 私有静态内部类
     */
    private static class SingletonFactory{
        private static CountCenter instance;
        private static AtomicInteger count = new AtomicInteger(0);
        static {
            System.out.println("静态内部类初始化" + SingletonFactory.class + " count:" + count.addAndGet(1));
            instance = new CountCenter();
        }
    }
    /**
     * 内部类模式 可靠
     */
    public static CountCenter getInstance(){
        return SingletonFactory.instance;
    }



    Map<String, Count> data = new ConcurrentHashMap<>();

    public void addAll(String key){
        Count count = data.getOrDefault(key, new Count());
        count.addAll();
        data.put(key, count);
    }
    public void addOk(String key){
        Count count = data.getOrDefault(key, new Count());
        count.addOk();
        data.put(key, count);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("CountCenter");
        for (String key : data.keySet()) {
            Count count = data.getOrDefault(key, new Count());
            stringBuilder.append("\n").append(key + "=" + count.toString());
        }
        return stringBuilder.toString();
    }

    class Count{
        long ctime = System.currentTimeMillis();
        long mtime = System.currentTimeMillis();

        AtomicLong all = new AtomicLong(0);
        AtomicLong ok = new AtomicLong(0);


        public Count addAll(){
            all.addAndGet(1);
            return this;
        }
        public Count addOk(){
            all.addAndGet(1);
            return this;
        }

        @Override
        public String toString() {
            return "Count " + TimeUtil.getTimeYmdHms(ctime) + " -> " + TimeUtil.getTimeYmdHms(mtime) + " " + ok + "/" + all + " dtime " + (mtime - ctime)
                    + " p " + (1000 * all.get() / (1 + mtime - ctime)) + " c/s "
                    ;
        }
    }







}
