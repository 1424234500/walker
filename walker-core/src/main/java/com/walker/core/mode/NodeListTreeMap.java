package com.walker.core.mode;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.walker.core.util.FileUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.*;
import java.util.stream.Collectors;

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
@Accessors(chain = true)
public class NodeListTreeMap<DATA> {

//    头节点
    NodeListTreeMapData<DATA> head = null;
    NodeListTreeMapData<DATA> tail = null;

    Fun< DATA> fun = null;
    public NodeListTreeMap(Fun<DATA> fun){
        this.fun = fun;
    }
    public NodeListTreeMap(NodeListTreeMapData<DATA> head, Fun<DATA> fun){
        this.fun = fun;
        this.head = head;
    }
    public void clear(){
        head = null;
        tail = null;
    }

    //    构建echarts json 绘图
    public String toHtmlEcharts(String filepath){
        FileUtil.mkdir(FileUtil.getFilePath(filepath));

        String template = FileUtil.readByLines("graph-template.html", null, null);


        List<EchartsNode> replaceNodesMake = new ArrayList<>();
        List<EchartsLink> replaceLinksMake = new ArrayList<>();
        traversalActionData((level, nodeParent, node, nodeChildRess) -> {
//            构造当前node的对应图节点 由于循环引用问题 多次构建节点必然性 可能导致重复所以去重

            if(node != null) {
                EchartsNode nowNode = fun.makeEchartsNode(node.data);
                if (!replaceNodesMake.contains(nowNode)) {
                    replaceNodesMake.add(nowNode);
                }
            }
//          构造父节点 与当前节点的连线  递归遍历 以关系为准 遍历
            if(nodeParent != null && node != null) {
                replaceLinksMake.add(fun.turnToEchartsLink(nodeParent.data, node.data));
            }
//            for (EchartsNode child : nodeChildRess) {
//                replaceLinksMake.add(fun.turnToEchartsLink(nowNode, child));
//            }
            return null;
        });

        template = template
                .replace("${replaceTitleText}", "MakeGraghOfJavaMap")
                .replace("${replaceNodesMake}", JSON.toJSONString(replaceNodesMake, SerializerFeature.PrettyFormat))
                .replace("${replaceLinksMake}", JSON.toJSONString(replaceLinksMake, SerializerFeature.PrettyFormat))
        ;

        FileUtil.saveAs(template, filepath, false);


        return filepath;
    }


    interface Fun<DATA> extends Comparator<DATA>{

        //       默认实现 仅需实现非null自定义比较
        int compareNotNull(DATA o1, DATA o2);
        boolean equalNotNull(DATA o1, DATA o2);


        default int compare(DATA o1, DATA o2){
            int res = 0;
            if(o1 == o2 || (o1==null && o2==null)) res = 0;
            else if(o1 == null) res = -1;
            else if(o2 == null) res = 1;
            else res = compareNotNull(o1, o2);
            return res;
        }

        default boolean equal(DATA o1, DATA o2){
            boolean res = false;
            if(o1 == o2 || (o1==null && o2==null)) res = true;
            else if(o1!=null && o2!=null) res = equalNotNull(o1, o2);
            return res;
        }
//        默认实现 以toString为值 其hash为id
        default EchartsNode makeEchartsNode(DATA data){
            String name = String.valueOf(data).replace('"', '\'');
            return new EchartsNode(name, Math.abs(name.hashCode())+"", 100);
        }
//        默认实现 以id相互为线名字
        default EchartsLink turnToEchartsLink(DATA parent, DATA child){
            if(parent == null || child == null){
                int i = 0;
            }
            EchartsNode from = makeEchartsNode(parent);
            EchartsNode to = makeEchartsNode(child);
            return new EchartsLink(from.getId(), to.getId(), makeEchartsLinkRelation(from, to));
        }
        default EchartsLinkRelation makeEchartsLinkRelation(EchartsNode from, EchartsNode to){
            String name = from.getName() + "->" + to.getName();
            return new EchartsLinkRelation(name, Math.abs(name.hashCode()) + "");
        }

        default DATA makeData(String excelValue){
            return null;
        }

    }


    protected Map<NodeListTreeMapData<DATA>, NodeListTreeMapData<DATA>> flag = new LinkedHashMap<>();

    /**
     * 兼容图 检测遍历
     * 遍历执行 多线程并发?
     * 深度优先 从低层一层一层向上构建
     * 处理数据data层级关系
     *
     *
     * 遍历所有关系 同节点可能出现多次 自主去重
     *
     */
    synchronized protected <RETURN> RETURN traversalActionData(NodeListTreeMapData.ActionData<DATA, RETURN> actionData) {
        flag.clear();
        return traversalActionDataImpl(null, head, 0, actionData);
    }

    protected  <RETURN> RETURN traversalActionDataImpl(NodeListTreeMapData<DATA> nodeParent, NodeListTreeMapData<DATA> node, int level, NodeListTreeMapData.ActionData<DATA, RETURN> actionData) {
        assert (node != null);

//        占坑
        flag.put(nodeParent, node);

        List<RETURN> childRess = new ArrayList<>();
//        处理子节点们 若子节点已经处理过则不再处理 但子节点关系依然返回null
        node.actionChilds(child -> {
            RETURN childRes = null;
            if( ! child.equals(flag.get(node))){
                childRes = traversalActionDataImpl(node, child, level + 1, actionData);
            }
            childRess.add(childRes);
        });

//        处理自己
        RETURN childRes = actionData.action(level, nodeParent, node, childRess);
        return childRes;
    }



    public NodeListTreeMapData<DATA> createNode(DATA dataNew) {
        return new NodeListTreeMapData<>(dataNew, fun);
    }


    /**
     * 添加新节点到某节点 树退化list模式 树最左最底层附加最左子节点
     * 适配头插法 haed最后遍历
     */
    public NodeListTreeMapData<DATA> addDataToParent(DATA parent, DATA dataNew) {
        NodeListTreeMapData<DATA> nodeNew = createNode(dataNew);
        NodeListTreeMapData<DATA> nodeAt = findNodeOneBottom(parent);
        return addNodeToParent(nodeAt, nodeNew);
    }
    public NodeListTreeMapData<DATA> addNodeToParent(NodeListTreeMapData<DATA> parent, NodeListTreeMapData<DATA> dataNew) {
//        查找到目标节点
        if(head == null){ //直接添加节点;
            head = dataNew;
            tail = head;
//            System.out.println("addChild head " + head);
            return head;
        }else{
//            System.out.println("addChild find" + parent + " -> " + nodeAt + " addChild " + dataNew);
            if(parent != null && parent.addChild(dataNew)){
                tail = dataNew;
                return dataNew;
            }
            return null;
        }
    }
    /**
     * 树最顶层 root优选
     */
    public NodeListTreeMapData<DATA> findNodeOneTop(DATA dataBy) {
        List<NodeListTreeMapData<DATA>> list = findNode(dataBy);
        if(list != null && list.size() > 0){
            return list.get((list.size() - 1));
        }
        return null;
    }
    public DATA findOneTop(DATA dataBy){
        NodeListTreeMapData<DATA> res = findNodeOneTop(dataBy);
        return res == null ? null : res.data;
    }
    public DATA findOneBottom(DATA dataBy){
        NodeListTreeMapData<DATA> res = findNodeOneBottom(dataBy);
        return res == null ? null : res.data;
    }
    /**
     * 树最底层
     */
    protected NodeListTreeMapData<DATA> findNodeOneBottom(DATA dataBy) {
        List<NodeListTreeMapData<DATA>> list = findNode(dataBy);
        if(list != null && list.size() > 0){
            return list.get((0));
        }
        return null;
    }

    /**
     * 节点查找 顶层在list后面
     */
    public List<NodeListTreeMapData<DATA>> findNode(DATA dataBy) {
        List<NodeListTreeMapData<DATA>> res = new ArrayList<>();
        if(head != null)
            traversalActionData(new NodeListTreeMapData.ActionData<DATA, Object>() {
                @Override
                public Object action(int level, NodeListTreeMapData<DATA> nodeParent, NodeListTreeMapData<DATA> node, List<Object> childRess) {
                    if(fun.equal(dataBy, node.data)){
                        res.add(node);
                    }
                    return null;
                }
            });
        return res;
    }

    public List<DATA> find(DATA dataBy) {
        return findNode(dataBy).stream().map(item -> item.data).collect(Collectors.toList());
    }






}

