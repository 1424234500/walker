package com.walker.design.component;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 菜单组件 树 叶子 节点
 *
 * 叶子
 */
public class ComponentLeaf extends Component {
    private static Logger log = LoggerFactory.getLogger(ComponentLeaf.class);

    String name;
    public ComponentLeaf(){
        name = getClass().getSimpleName();
    }
    public ComponentLeaf setName(String name){
        this.name = name;
        return this;
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Boolean isLeaf() {
        return true;
    }

    @Override
    public void print() {
        super.print();
    }
}



