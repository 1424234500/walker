package com.walker.demo;

import com.walker.util.Bean;
import com.walker.util.ThreadUtil;
import com.walker.util.Tools;
import com.walker.util.Watch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 测试并行处理
 */
public class TestJdk8ParalleStream {

    static int sleep = 1000;
    static Map<Thread, AtomicInteger> map = new ConcurrentHashMap<>();

    public static void main(String[] args) {

//        testParalStream();
        testParalStreamTree();
    }

    private static void testParalStreamTree() {
        Bean root = makeTree(new Bean().put("name", "root"), 1, 4);
        Watch watch = new Watch("cost");
        showTreeParallelStream(root, 1);
        show();
        watch.cost("showTreeParallelStream");
//        Tools.out("-----------");
//        showTree(root, 1);
//        show();
//        watch.cost("showTree");
        Tools.out(watch.toString());

    }

    //    cpu密集型
    private static void showTreeParallelStream(Bean node, int depth) {
//        Tools.out(Tools.fillStringBy("", " ", depth*6, 0) + node.get("name", ""));
//        Tools.out(node.get("name"));
        List<Bean> child = node.get("child", new ArrayList<>());
//        for (Bean bean : child) {
//            showTree(bean, depth + 1);
//        }
        child.parallelStream().forEach(bean -> {
            showTreeParallelStream(bean, depth + 1);
        });
        ThreadUtil.sleep(sleep);
        add(depth);

    }

    //    cpu密集型
    private static void showTree(Bean node, int depth) {
        add(depth);
        ThreadUtil.sleep(sleep);
//        Tools.out(Tools.fillStringBy("", " ", depth*6, 0) + node.get("name", ""));
//        Tools.out(node.get("name"));
        List<Bean> child = node.get("child", new ArrayList<>());
        for (Bean bean : child) {
            showTree(bean, depth + 1);
        }
    }

    //    node有i个节点 最大maxI级别
    private static Bean makeTree(Bean node, int i, int maxI) {
//        root
//                a
//                    1
//                    2
//                    3
//                b
//                    1
//                    2
//                    3
        if (i <= maxI) {
            List<Bean> list = new ArrayList<>();
            for (int a = 0; a <= i; a++) {
                Bean bean = new Bean();
                bean.set("name", node.get("name") + "/" + (char) ('a' + i) + a);

                makeTree(bean, i + 1, maxI);

                list.add(bean);
            }
            node.set("child", list);
        }
        return node;
    }

    synchronized static void add(int depth) {
        AtomicInteger ai = map.get(Thread.currentThread());
        if (ai == null) {
            ai = new AtomicInteger(0);
            map.put(Thread.currentThread(), ai);
        }
        Tools.out(ai + " depth " + depth);
        ai.addAndGet(1);
    }

    static void show() {
        int all = 0;
        for (AtomicInteger atomicInteger : map.values()) {
            all += atomicInteger.get();
        }
        for (Thread key : map.keySet()) {
            Tools.out(key.getName(), (int) (1f * map.get(key).get() * 100 / all) + "%", map.get(key).get(), all);
        }
//        Tools.out(map);
        map.clear();
    }

    private static void testParalStream() {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        for (int i = 0; i < 10; i++) {
            list.subList(0, list.size() * i / 10).parallelStream().forEach(item -> {
                add(0);
//            Tools.out(item);
            });
        }
    }


}
