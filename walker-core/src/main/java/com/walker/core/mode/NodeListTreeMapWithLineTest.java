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
public class NodeListTreeMapWithLineTest<DATA> {
    static int size = 6;

    static String key = "key";
    static String priorty = "priorty";

    public static void main(String[] argv) {
        NodeListTreeMapWithLine<BeanLinked, Line> list = new NodeListTreeMapWithLine<BeanLinked, Line>(new NodeListTreeMap.Fun<BeanLinked>() {
            @Override
            public int compareNotNull(BeanLinked o1, BeanLinked o2) {
                return o1.get(priorty, 0).compareTo(o2.get(priorty, 0));
            }

            @Override
            public boolean equalNotNull(BeanLinked o1, BeanLinked o2) {
                return o1.get(key, "").equals(o2.get(key, ""));
            }

            @Override
            public EchartsNode makeEchartsNode(BeanLinked data) {
                return new EchartsNode(data == null ? null : data.get("name", ""), data == null ? null : data.get(key, ""), 50);
            }

            @Override
            public EchartsLinkRelation makeEchartsLinkRelation(EchartsNode from, EchartsNode to) {
                String name = (from == null ? null : from.getId())
                        + "->" +
                        (to == null ? null : to.getId());
                return new EchartsLinkRelation(name, Math.abs(name.hashCode()) + "");
            }
        });

        testList(list);
        testTree(list);
        testMap(list);
        testExcel(list);

        new TreeMap();
        new ArrayList<>();
    }

    private static void testList(NodeListTreeMapWithLine<BeanLinked, Line> list) {
        list.clear();

        int cc = 0;
        list.addDataToParent(null, new BeanLinked().put("name", 0).put(key, cc++).put(priorty, cc).put("x", 0).put("y", 0));
        NodeListTreeMapData<BeanLinked> root = list.head;

        List<NodeListTreeMapData<BeanLinked>> nodes = new ArrayList<>();
        nodes.add(root);
        for(int i = 0; i < 14; i++){
            nodes.add(list.createNode(new BeanLinked().put("name", cc++).put(key, cc).put(priorty, cc).put("x", 0).put("y", 0)));
        }
        Integer[][] lines = {
//                二叉树
                {1}
                , {2}
                , {3}
                , {4}
                , {5}
                , {6}
                , {7}
                , {8}
                , {9}
                , {10}
                , {11}
        };
        for (int i = 0; i < nodes.size() && i < lines.length; i++) {
            for (int i1 = 0; i1 < lines[i].length; i1++) {
                list.addNodeToParent(nodes.get(i), nodes.get(lines[i][i1]), new Line(i+"->"+i1, i, i1));
            }
        }

        list.traversalActionData((NodeListTreeMapData.ActionData<BeanLinked, BeanLinked>) (level, nodeParent, node, childNodeRess) -> {

            System.out.println("level " + level + " " +
                    (nodeParent == null ? null : nodeParent.data.get(key))
                    + "->" +
                    (node == null ? null : node.data.get(key))
                    + " childs : " + childNodeRess);

            return null;
        });

        list.toHtmlEcharts("make01-list.html");
    }

    private static void testTree(NodeListTreeMapWithLine<BeanLinked, Line> list) {
        list.clear();

        int cc = 0;
        list.addDataToParent(null, new BeanLinked().put("name", 0).put(key, cc++).put(priorty, cc).put("x", 0).put("y", 0));
        NodeListTreeMapData<BeanLinked> root = list.head;

        List<NodeListTreeMapData<BeanLinked>> nodes = new ArrayList<>();
        nodes.add(root);
        for(int i = 0; i < 14; i++){
            nodes.add(list.createNode(new BeanLinked().put("name", cc++).put(key, cc).put(priorty, cc).put("x", 0).put("y", 0)));
        }
        Integer[][] lines = {
//                二叉树
                {1, 2}
                , {3, 4}
                , {5, 6}
                , {7, 8}
                , {9, 10}
                , {11, 12}
                , {13, 14}
        };
        for (int i = 0; i < nodes.size() && i < lines.length; i++) {
            for (int i1 = 0; i1 < lines[i].length; i1++) {
                list.addNodeToParent(nodes.get(i), nodes.get(lines[i][i1]), new Line(i+"->"+i1, i, i1));
            }
        }

        list.traversalActionData((NodeListTreeMapData.ActionData<BeanLinked, BeanLinked>) (level, nodeParent, node, childNodeRess) -> {

            System.out.println("level " + level + " " +
                    (nodeParent == null ? null : nodeParent.data.get(key))
                    + "->" +
                    (node == null ? null : node.data.get(key))
                    + " childs : " + childNodeRess);

            return null;
        });


        list.toHtmlEcharts("make02-tree.html");
    }
    private static void testMap(NodeListTreeMapWithLine<BeanLinked, Line> list) {
        list.clear();

        int size = 7;
        int cc = 0;
        list.addDataToParent(null, new BeanLinked().put("name", 0).put(key, cc++).put(priorty, cc).put("x", 0).put("y", 0));
        NodeListTreeMapData<BeanLinked> root = list.head;

        List<NodeListTreeMapData<BeanLinked>> nodes = new ArrayList<>();
        nodes.add(root);
        for(int i = 0; i < size; i++){
            nodes.add(list.createNode(new BeanLinked().put("name", cc++).put(key, cc).put(priorty, cc).put("x", 0).put("y", 0)));
        }
        Integer[][] lines = {
//                二叉树
                {1, 2}
                , {3, 4}
                , {5, 6}
//                b+树
                , {4}
                , {5}
                , {6}
//                回环
                , {5}
        };
        for (int i = 0; i < nodes.size() && i < lines.length; i++) {
            for (int i1 = 0; i1 < lines[i].length; i1++) {
                list.addNodeToParent(nodes.get(i), nodes.get(lines[i][i1]), new Line(i+"->"+i1, i, i1));
            }
        }

        list.toHtmlEcharts("make03-map.html");
    }

    private static void testExcel(NodeListTreeMapWithLine<BeanLinked, Line> list) {
        list.clear();
        list.parseExcel("map.xlsx", new NodeListTreeMapWithLine.ExcelParse<BeanLinked, Line>() {
            @Override
            public BeanLinked makeData(int i, int j, String excelValue) {
                return new BeanLinked()
                        .put(key, excelValue + "-" + i + "-" + j)
                        .put("name", "(" + i + "," + j + ")-" + (excelValue.length() > 0 ? excelValue : "0"))
                        ;
            }

            @Override
            public Line makeLine(int i, int j, BeanLinked parent, BeanLinked data) {
                return new Line("(" + i + "," + j + ")", i, j);
            }

        });
        list.toHtmlEcharts("make04-excel.html");

        list.clear();
        list.parseExcel("map1.xlsx", new NodeListTreeMapWithLine.ExcelParse<BeanLinked, Line>() {
            @Override
            public BeanLinked makeData(int i, int j, String excelValue) {
                return excelValue != null && excelValue.length() > 0 ? new BeanLinked()
                        .put(key, excelValue + "-" + i + "-" + j)
                        .put("name",   //"(" + i + "," + j + ")-" +
                                (excelValue.length() > 0 ? excelValue : "0"))

                        : null

                        ;
            }

            @Override
            public Line makeLine(int i, int j, BeanLinked parent, BeanLinked data) {
                return new Line("(" + i + "," + j + ")", i, j);
            }

        });
        list.toHtmlEcharts("make041-excel.html");

    }
}
