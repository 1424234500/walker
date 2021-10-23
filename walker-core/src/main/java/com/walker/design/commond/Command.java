package com.walker.design.commond;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 命令模式
 * 多插槽开关问题     队列问题 日志请求问题
 * <p>
 * 将请求封装成对象，让使用不同的请求、队列、日志，参数华，撤销功能
 * <p>
 * 松耦合
 */
public abstract class Command {
    Logger log = LoggerFactory.getLogger(Command.class);


    Item item;

    public void executeOn() {
        if (item != null) {
            item.on();
        }
    }

    public void executeOff() {
        if (item != null) {
            item.off();
        }
    }

    public Item getItem() {
        return item;
    }

    public Command setItem(Item item) {
        this.item = item;
        return this;
    }
}
