package com.walker.design;

import com.walker.design.commond.ItemDoor;
import com.walker.design.commond.ItemLight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 外观模式 多个接口封装为一个接口
 * <p>
 * 看电影问题 很多配置的组合
 * <p>
 * 结合命令模式的组合 宏命令 macro
 */
public class Facade {
    Logger log = LoggerFactory.getLogger(Facade.class);
    ItemDoor itemDoor;
    ItemLight itemLight;

    public Facade(ItemDoor itemDoor, ItemLight itemLight) {
        this.itemDoor = itemDoor;
        this.itemLight = itemLight;
    }

    public static void main(String[] argvs) {
        Facade facade = new Facade(new ItemDoor(), new ItemLight());
        facade.watchMovie();
        facade.close();
    }

    public void watchMovie() {
        log.info("watchMovie----------");

        this.itemDoor.on();
        this.itemLight.on();

    }

    public void close() {
        log.info("close----------");

        this.itemDoor.off();
        this.itemLight.off();
    }


}
