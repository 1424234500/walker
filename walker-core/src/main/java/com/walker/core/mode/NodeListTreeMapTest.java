package com.walker.core.mode;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 多数据结构融合特性 Node List Tree Map
 * 图形化预演支持
 *
 * 双向tree
 * 多节点tree
 *
 * 泛型数据携带 组合 or 继承 has a ? is a  不希望新构建类 希望接口反转   实体类和组合接口实现 继承扩展效果
 *
 * 实现红黑树规则
 * 实现树遍历递归 深度 广度
 * 实现查找
 *
 */

/**
 *
 * @param <DATA> 数据节点查找 依赖equal
 *              数据节点优先级 依赖compare
 */
@Data
public class NodeListTreeMapTest<DATA> {

    static String key = "key";
    static String priorty = "priorty";

    public static void main(String[] argv){
        NodeListTreeMap<BeanLinked> list = new NodeListTreeMap<>(new NodeListTreeMap.Fun<BeanLinked>() {
            @Override
            public int compareNotNull(BeanLinked o1, BeanLinked o2) {
                return o1.get(priorty, 0).compareTo(o2.get(priorty, 0));
            }

            @Override
            public boolean equalNotNull(BeanLinked o1, BeanLinked o2) {
                return o1.get(key, "").equals(o2.get(key, ""));
            }

        });

//        testList(list);
//        testTree(list);
        testMap(list);
        new TreeMap();
        new ArrayList<>();
    }
    private static void add(NodeListTreeMap<BeanLinked> list, int i, int maxi, BeanLinked parent){
        if(i >= maxi){
            return;
        }
        List<BeanLinked> adds = new ArrayList<>();
        for(int cc = 0; cc < i; cc++){
            BeanLinked item = new BeanLinked().put(key, i + "/" + maxi + "/" + cc).put(priorty, cc);
            list.addDataToParent(parent, item);
            adds.add(item);
        }

        for (BeanLinked add : adds) {
            add(list, i+1, maxi, add);
        }




    }

    private static void testTree(NodeListTreeMap<BeanLinked> list) {
        int size = 5;

        add(list, 1, size, null);
//        构造循环异常树 即为图
        NodeListTreeMapData<BeanLinked> data1 = list.findNodeOneTop(new BeanLinked().put(key, "1/5/0"));
        NodeListTreeMapData<BeanLinked> data2 = list.findNodeOneTop(new BeanLinked().put(key, "2/5/0"));
        list.addNodeToParent(data2, data1);
        list.traversalActionData(new NodeListTreeMapData.ActionData<BeanLinked, Object>() {
            @Override
            public Object action(int level, NodeListTreeMapData<BeanLinked> nodeParent, NodeListTreeMapData<BeanLinked> node, List<Object> nodeChildRess) {
                System.out.println(level + " " + node);
                return null;
            }
        });

    }
    private static void testList(NodeListTreeMap<BeanLinked> list) {
        int size = 5;

        for(int i = size - 1; i >= 0; i--){
            BeanLinked bean = new BeanLinked().put(key, key + i).put(priorty, i);
            list.addDataToParent(null, bean);
        }

        list.traversalActionData(new NodeListTreeMapData.ActionData<BeanLinked, Object>() {
            @Override
            public Object action(int level, NodeListTreeMapData<BeanLinked> nodeParent, NodeListTreeMapData<BeanLinked> node, List<Object> nodeChildRess) {
                System.out.println(level + " " + node);
                return null;
            }
        });
    }


    private static void testMap(NodeListTreeMap<BeanLinked> list) {
        int size = 5;
        int cc = 0;
        list.addDataToParent(null, new BeanLinked().put(key, cc++).put(priorty, cc).put("x", 0).put("y", 0));
        NodeListTreeMapData<BeanLinked> root = list.head;


        NodeListTreeMapData<BeanLinked> a1 = list.createNode(new BeanLinked().put(key, cc++).put(priorty, cc).put("x", 20).put("y", 0));
        NodeListTreeMapData<BeanLinked> a2 = list.createNode(new BeanLinked().put(key, cc++).put(priorty, cc).put("x", 20).put("y", 10));
        NodeListTreeMapData<BeanLinked> a3 = list.createNode(new BeanLinked().put(key, cc++).put(priorty, cc).put("x", 30).put("y", 10));
        NodeListTreeMapData<BeanLinked> a4 = list.createNode(new BeanLinked().put(key, cc++).put(priorty, cc).put("x", 30).put("y", 20));
        NodeListTreeMapData<BeanLinked> a5 = list.createNode(new BeanLinked().put(key, cc++).put(priorty, cc).put("x", 20).put("y", 30));
        NodeListTreeMapData<BeanLinked> a6 = list.createNode(new BeanLinked().put(key, cc++).put(priorty, cc).put("x", 10).put("y", 30));
        NodeListTreeMapData<BeanLinked> a7 = list.createNode(new BeanLinked().put(key, cc++).put(priorty, cc).put("x", 0).put("y", 30));
        NodeListTreeMapData<BeanLinked> a8 = list.createNode(new BeanLinked().put(key, cc++).put(priorty, cc).put("x", 40).put("y", 40));
        NodeListTreeMapData<BeanLinked> a9 = list.createNode(new BeanLinked().put(key, cc++).put(priorty, cc).put("x", 50).put("y", 40));
        NodeListTreeMapData<BeanLinked> a10 = list.createNode(new BeanLinked().put(key, cc++).put(priorty, cc).put("x", 40).put("y", 50));

        root.addChild(a1);
        root.addChild(a2);

        a1.addChild(a3);
        a1.addChild(a4);

        a2.addChild(a5);
        a2.addChild(a6);

        a5.addChild(a1);
        a6.addChild(a2);

        a5.addChild(a7);
        a7.addChild(a8);

        a5.addChild(a9);
        a9.addChild(a10);


        list.traversalActionData(new NodeListTreeMapData.ActionData<BeanLinked, Object>() {
            @Override
            public Object action(int level, NodeListTreeMapData<BeanLinked> nodeParent, NodeListTreeMapData<BeanLinked> node, List<Object> nodeChildRess) {
                System.out.println(level + " " + node);
                return null;
            }
        });




    }
}

