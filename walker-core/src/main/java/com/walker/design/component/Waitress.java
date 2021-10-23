package com.walker.design.component;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 *  组合模式    将对象组成树形结构 层次 让客户一致处理不同对象和对象组合
 *
 *          透明性和安全性的取舍
 *
 *          屏蔽遍历细节
 *
 *      服务员菜单问题
 *
 *  早餐 午餐 晚餐各个菜单存储结构不一致 导致遍历问题
 *  添加菜单
 *      子菜单问题   树形结构
 *
 *
 *
 *
 */
public class Waitress<T> {
    private static Logger log = LoggerFactory.getLogger(Waitress.class);

//    Iterator<T> menuBreakfasts;
//    Iterator<T> menuLaunch;

    Component component;

    public Waitress() {
    }

    public Component getComponent() {
        return component;
    }

    public Waitress<T> setComponent(Component component) {
        this.component = component;
        return this;
    }

    public void printMenu(){
        log.info("----------printMenu----------");
//        printMenu(menuBreakfasts);
//        printMenu(menuLaunch);
        component.print();

    }
    private void printMenu(Iterator<T> iterator){
        while(iterator.hasNext()){
            log.info("menu:" + iterator.next());
        }
    }



//    public Iterator<T> getMenuBreakfasts() {
//        return menuBreakfasts;
//    }
//
//    public Waitress<T> setMenuBreakfasts(Iterator<T> menuBreakfasts) {
//        this.menuBreakfasts = menuBreakfasts;
//        return this;
//    }
//
//    public Iterator<T> getMenuLaunch() {
//        return menuLaunch;
//    }
//
//    public Waitress<T> setMenuLaunch(Iterator<T> menuLaunch) {
//        this.menuLaunch = menuLaunch;
//        return this;
//    }
}



