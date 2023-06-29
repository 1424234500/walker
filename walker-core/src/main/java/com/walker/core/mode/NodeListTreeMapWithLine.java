package com.walker.core.mode;


import com.walker.core.util.Excel;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
 * 组合 or 继承
 *
 * @param <DATA> 数据节点查找 依赖equal
 *              数据节点优先级 依赖compare
 *
 *              维护 图之间的线关系
 */
@Data
public class NodeListTreeMapWithLine<DATA, LINE> extends NodeListTreeMap<DATA> {

    LinkedHashMap<NodeListTreeMapData<DATA>, LinkedHashMap<NodeListTreeMapData<DATA>, LINE>> lines = new LinkedHashMap<>();



    public NodeListTreeMapWithLine(NodeListTreeMap.Fun<DATA> fun) {
        super(fun);
    }

    public NodeListTreeMapWithLine(NodeListTreeMapData<DATA> head, NodeListTreeMap.Fun<DATA> fun) {
        super(head, fun);
    }
    public boolean addNodeToParent(NodeListTreeMapData<DATA> parent, NodeListTreeMapData<DATA> child, LINE line){
        super.addNodeToParent(parent, child);
        setLine(parent, child, line);
        return true;
    }
    public boolean addDataToParent(DATA parent, DATA child, LINE line){
        super.addDataToParent(parent, child);
        setLine(parent, child, line);
        return true;
    }
    public void setLine(DATA from, DATA to, LINE line) {
        NodeListTreeMapData<DATA> nfrom = findNodeOneBottom(from);
        NodeListTreeMapData<DATA> nto = findNodeOneBottom(to);
        setLine(nfrom, nto, line);
    }
    public void setLine(NodeListTreeMapData<DATA> from, NodeListTreeMapData<DATA> to, LINE line) {
        LinkedHashMap<NodeListTreeMapData<DATA>, LINE> childLineList = lines.getOrDefault(from, new LinkedHashMap<>());
        if(line == null) {
            childLineList.remove(to);
        }else {
            childLineList.put(to, line);
        }
        lines.put(from, childLineList);
    }
    public LINE getLine(NodeListTreeMapData<DATA> from, NodeListTreeMapData<DATA> to, LINE lineDefault){
        LinkedHashMap<NodeListTreeMapData<DATA>, LINE> childLineList = lines.getOrDefault(from, new LinkedHashMap<>());
        LINE res = childLineList.get(to);
        if(res == null){
            res = lineDefault;
        }
        return res;
    }

    interface ExcelParse<DATA, LINE> {
        DATA makeData(int i, int j, String excelValue);

        LINE makeLine(int i, int j, DATA parent, DATA data);
    }

    public void parseExcel(String filepath, ExcelParse<DATA, LINE> parser){
        List<List<String>> excel = new Excel(filepath).read().getSheet(0);
        for (List<String> strings : excel) {
            System.out.println(strings);
        }
//        二维矩阵遍历生成map
        List<List<DATA>> datas = new ArrayList<>();
        for (int i = 0; i < excel.size() ; i++) {
            List<DATA> ls = new ArrayList<>();
            for(int j = 0; j < excel.get(i).size(); j++){
                ls.add(parser.makeData(i, j, excel.get(i).get(j)));
            }
            datas.add(ls);
        }


//        list.clear();
//        list.addNodeToParent(null, list.createNode(new BeanLinked())); //初始化一个头
        makeMap(this, this.getHead(), 0, 0, datas, "child", new Bean(), parser);

    }


    //    为该节点处理周边关系
    private void makeMap(NodeListTreeMapWithLine<DATA, LINE> list, NodeListTreeMapData<DATA> node, int i, int j, List<List<DATA>> excel, String info, Bean bean, ExcelParse<DATA, LINE> parser) {
        if(i >= excel.size() || j >= excel.get(0).size() || i< 0 || j < 0){
            System.out.println("border" +  i + " " + j);
            return;
        }

        DATA data = excel.get(i).get(j);
        if(data == null){
            return; //null节点不参与制图
        }
//        挂载 当前节点
        if(node == null){
            list.addNodeToParent(null, list.createNode(data));
        }else {
            list.addDataToParent(node.data, data, parser.makeLine(i, j, node.data, data));
        }
        NodeListTreeMapData<DATA> newNode = list.findNodeOneTop(data);

//        控制节点传递 只一次
        String his = "his-" + i + "-" + j;
        if(bean.get(his, "").length() > 0){
            return;
        }
        bean.put(his, "aa");
//        System.out.println("now " + his);

//        处理当前节点子节点们
        makeMap(list, newNode, i+1, j, excel, "right", bean, parser);
        makeMap(list, newNode, i, j+1, excel, "top", bean, parser);
        makeMap(list, newNode, i-1, j, excel, "left", bean, parser);
        makeMap(list, newNode, i, j-1, excel, "bottom", bean, parser);

    }

}
