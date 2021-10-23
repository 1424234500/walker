package com.walker.design.commond;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 遥控器
 * 多槽位
 * 回滚栈
 */
public class ControlRemote {
    Logger log = LoggerFactory.getLogger(ControlRemote.class);

    /**
     * 多个槽位
     */
//    List<Command> slots;
    Map<Integer, Command> slots;
    /**
     * 回滚命令栈
     */
    Stack<Command> stackCommonds;
    /**
     * 回滚状态栈
     */
    Stack<Integer> stackStatus;

    /**
     * 栈最大深度
     */
    int stackSize = 8;

    ControlRemote(int size, int stackSize) {
        assert size > 0;

        this.stackSize = stackSize;
//        this.slots = new ArrayList<>();
        this.slots = new ConcurrentHashMap<>();
        stackCommonds = new Stack<>();
        stackStatus = new Stack<>();

//        for(int i = 0; i < size; i++){
//            this.slots.add(null);
//        }
    }

    private void pop() {
        this.stackStatus.pop();
        this.stackCommonds.pop();
    }

    private void push(Command command, Integer status) {
        if (this.stackSize < this.stackStatus.size()) {
            log.info("stack full remove last " + this.stackStatus.size());
            this.stackStatus.remove(0);
            this.stackCommonds.remove(0);

        }
        this.stackCommonds.push(command);
        this.stackStatus.push(status);
    }

    /**
     * 开
     *
     * @param slot
     */
    public void buttonWasPressed(int slot) {
        Command command = this.slots.get(slot);
        if (slots.get(slot) != null) {
            log.info("--------------");
            command.executeOn();
            push(command, 0);
        } else {
            log.error("slot " + slot + " is null");
        }
    }

    /**
     * 关
     *
     * @param slot
     */
    public void buttonWasPushed(int slot) {
        Command command = this.slots.get(slot);
        if (command != null) {
            log.info("--------------");
            command.executeOff();
            push(command, 1);
        } else {
            log.error("slot " + slot + " is null");
        }
    }

    /**
     * 回滚
     */
    public void buttonWasBack() {
        log.info("back " + this.stackStatus.size());
        Command command = this.stackCommonds.peek();
        Integer integer = this.stackStatus.peek();
        if (integer == 1) {
            command.executeOn();
        } else {
            command.executeOff();
        }
        pop();
    }

    /**
     * 回滚所有
     */
    public void buttonWasBackAll(Integer size) {
        size = Math.min(size, this.stackStatus.size());
        while (size-- > 0) {
            buttonWasBack();
        }
    }

    public Command getSlot(int slot) {
        return slots.get(slot);
    }

    public ControlRemote setSlot(int slot, Command command) {
//        for(int i = this.slots.size(); slot >= this.slots.size(); i++){//扩容
//            this.slots.add(null);
//        }
//        this.slots.remove(slot);
//        this.slots.add(slot, command);
        this.slots.put(slot, command);
        return this;
    }
}



