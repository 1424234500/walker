package com.walker.design.iterator;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * 迭代器模式           统一实现迭代器iterator接口
 * <p>
 * 屏蔽遍历细节
 * <p>
 * 服务员菜单问题
 * <p>
 * 早餐 午餐 晚餐各个菜单存储结构不一致 导致遍历问题
 * 添加菜单
 * 子菜单问题   树形结构
 */
public class Waitress<T> {
    private static final Logger log = LoggerFactory.getLogger(Waitress.class);

    Iterator<T> menuBreakfasts;
    Iterator<T> menuLaunch;


    public Waitress() {
    }


    public void printMenu() {
        log.info("----------printMenu----------");
        printMenu(menuBreakfasts);
        printMenu(menuLaunch);

    }

    private void printMenu(Iterator<T> iterator) {
        while (iterator.hasNext()) {
            log.info("menu:" + iterator.next());
        }
    }


    public Iterator<T> getMenuBreakfasts() {
        return menuBreakfasts;
    }

    public Waitress<T> setMenuBreakfasts(Iterator<T> menuBreakfasts) {
        this.menuBreakfasts = menuBreakfasts;
        return this;
    }

    public Iterator<T> getMenuLaunch() {
        return menuLaunch;
    }

    public Waitress<T> setMenuLaunch(Iterator<T> menuLaunch) {
        this.menuLaunch = menuLaunch;
        return this;
    }
}



