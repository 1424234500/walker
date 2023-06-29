package com.walker.core.mode;


import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

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
public class NodeListTreeMapData<DATA> {

    DATA data = null;

//    单向
//    优先级子节点  权重线问题? 一层数据只做一层事  wait 再次封装
    PriorityQueue<NodeListTreeMapData<DATA>> childsPriority;




    public PriorityQueue<NodeListTreeMapData<DATA>> getChildsPriorityCopy(){
        return new PriorityQueue<>(childsPriority);
    }

    /**
     * 遍历操作子节点
     * @param actionChild
     * @return 尾巴节点
     */
    public NodeListTreeMapData<DATA> actionChilds(ActionChild<DATA> actionChild){
        PriorityQueue<NodeListTreeMapData<DATA>> priorityQueue = getChildsPriorityCopy();
//        队列迭代器无法保证顺序
//        采用 复制队列 遍历
        NodeListTreeMapData<DATA> last = null;
        while (priorityQueue.size() > 0) {
            last = priorityQueue.poll();
            if(actionChild != null) {
                actionChild.action(last);
            }
        }
        return last;
    }

    /**
     * @return 最左节点 头节点
     */
    NodeListTreeMapData<DATA> getTreeLeft(){
        if(childsPriority.size() > 0){
            return childsPriority.peek();
        }
        return null;
    }

    /**
     * @return 最右节点 尾节点
     */
    NodeListTreeMapData<DATA> getTreeRight(){
        return actionChilds(null);
    }


    /**
     * 添加子节点
     * @param child
     */
    public boolean addChild(NodeListTreeMapData<DATA> child){
        return childsPriority.add(child);
    }



    public NodeListTreeMapData(DATA data, Comparator<DATA> comparator){
        this.data = data;
        childsPriority = new PriorityQueue<>((o1, o2) -> comparator.compare(o1.data, o2.data));
    }


    interface ActionChild<DATA>{
        void action(NodeListTreeMapData<DATA> child);
    }

    interface ActionData<DATA, RETURN>{
//        default
        RETURN action(int level, NodeListTreeMapData<DATA> nodeParent, NodeListTreeMapData<DATA> node, List<RETURN> nodeChildRess);
//        {
//            System.out.println(level);
//            System.out.println(node);
//            System.out.println(data);
//            System.out.println(childRess);
//            return null;
//        }
    }


    @Override
    public String toString() {
        return "NodeListTreeMapData{" +
                "childSize=" + childsPriority.size() +
                ", data=" + data +
                '}';
    }
}
