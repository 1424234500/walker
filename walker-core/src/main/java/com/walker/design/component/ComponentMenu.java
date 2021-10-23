package com.walker.design.component;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 菜单组件 树 叶子 节点
 *
 *  非叶子
 *
 */
public class ComponentMenu extends Component {
    private static Logger log = LoggerFactory.getLogger(ComponentMenu.class);
    Map<String, Component> map;

    String name;
    public ComponentMenu(){
        name = getClass().getSimpleName();
        map = new LinkedHashMap<>();
    }
    public ComponentMenu setName(String name){
        this.name = name;
        return this;
    }

    @Override
    public Component add(Component component) {
        map.put(component.getName(), component);
        return this;
    }

    @Override
    public Component remove(Component component) {
        map.remove(component.getName());
        return this;
    }

    @Override
    public Component getChild(String key) {
        return map.get(key);
    }

    @Override
    public int childSize() {
        return map.size();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Boolean isLeaf() {
        return false;
    }

    @Override
    public void print(){
        if(isLeaf()){
            log.info("Leaf:" + getName());
        }else{
            log.info("Node:" + getName() + " childs:" + childSize() + "---------");
            int i = 0;
            for(Component component : map.values()){
                component.print();
            }
            log.info("--------------------------------------------------------");
        }
    }
}



