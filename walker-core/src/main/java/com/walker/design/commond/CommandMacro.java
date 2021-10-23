package com.walker.design.commond;

/**
 * 命令模式
 * 多插槽开关会滚问题
 * <p>
 * 组合命令
 */
public class CommandMacro extends Command {

    Item[] items;

    @Override
    public void executeOn() {
        log.info("macro on----------");
        for (Item item : items) {
            if (item != null) {
                item.on();
            }
        }
    }

    @Override
    public void executeOff() {
        log.info("macro off----------");

        for (Item item : items) {
            if (item != null) {
                item.off();
            }
        }
    }

    public Item[] getItems() {
        return items;
    }

    public CommandMacro setItems(Item... items) {
        this.items = items;
        return this;
    }
}
