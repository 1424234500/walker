package com.walker.demo.lock;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 死锁案例
 * 抢占资源 哲学家就餐惨案
 */
public class AgLockDead {


    List<Object> resources = Arrays.asList("a b c".split(" +"));

    public static void main(String[] args) throws Exception {
        new AgLockDead().test();

    }

    //  随机顺序依次获取所需资源  本人先取左手还是右手的资源 扩展n资源
    public boolean get(List<Object> mylist) {
        Object tryNow = mylist.get((int) (Math.random() * mylist.size()));
        System.out.println(Thread.currentThread().getName() + " try " + tryNow + " of " + mylist);

        synchronized (tryNow) { //获取成功
            System.out.println(Thread.currentThread().getName() + " get ok" + tryNow + " of " + mylist);
            mylist.remove(tryNow);
            if (mylist.size() <= 0) { //所有都获取完毕
                return true;
            } else {
                return get(mylist);
            }
        }
    }

    void test() throws Exception {
        for (int i = 0; i < 8; i++)
            new Thread() {
                public void run() {

                    List<Object> mylist = new ArrayList<>(resources);
                    if (get(mylist)) {
                        System.out.println(Thread.currentThread().getName() + " get all res " + System.currentTimeMillis() + " ");
                    }

                }
            }.start();

    }


}
