package com.walker.design.vall_duck;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * 复合模式     同时使用多种模式来处理问题
 *
 *      鸡鸭鹅 组合问题
 *
 *
 * 鸭子s模板
 *
 *      组合模式    &   迭代器模式
 *
 */
public class DucksComponet extends Duck {
    Set<Quackable> sets = new HashSet<>();
    public DucksComponet add(Quackable quackable){
        sets.add(quackable);
        return this;
    }

    /**
     * 抽象功能 叫   迭代器模式
     */
    @Override
    public void quack() {
        log.info(getClass().getSimpleName() + " quacks " + sets.size());
        //鸭群通知
        this.setChanged();
        this.notifyObservers(getClass().getSimpleName());

        Iterator<Quackable> iterator = sets.iterator();
        while(iterator.hasNext()){
            Quackable quackable = iterator.next();
            quackable.quack();
//鸭群每个鸭子都需要通知
            this.setChanged();
            this.notifyObservers(" by componet " + quackable.getClass().getSimpleName());
        }

    }


}



