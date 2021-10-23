package com.walker.design.component;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 菜单组件 树 叶子 节点
 *
 */
public abstract class Component {
    private static Logger log = LoggerFactory.getLogger(Component.class);

    public Component add(Component component){
        throw new UnsupportedOperationException();
    }
    public Component remove(Component component){
        throw new UnsupportedOperationException();
    }
    public Component getChild(String key){
        throw new UnsupportedOperationException();
    }
    public String getName(){
        return getClass().getSimpleName();
    }
    public Boolean isLeaf(){
        return false;
    }
    public int childSize(){
        return 0;
    }

    public void print(){
        if(isLeaf()){
            log.info("Leaf:" + getName());
        }else{
            log.info("Node:" + getName() + " childs:" + childSize() );
        }
    }



}



