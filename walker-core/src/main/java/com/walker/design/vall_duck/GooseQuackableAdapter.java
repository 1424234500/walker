package com.walker.design.vall_duck;


/**
 *
 * 复合模式     同时使用多种模式来处理问题
 *
 *      鸡鸭鹅 组合问题
 *
 *
 * 鹅模板 适配器 让支持鸭子叫
 *
 *      适配器模式
 *
 */
public class GooseQuackableAdapter implements Quackable {

    Honkable honkable;

    public Honkable getHonkable() {
        return honkable;
    }

    public GooseQuackableAdapter setHonkable(Honkable honkable) {
        this.honkable = honkable;
        return this;
    }

    /**
     * 抽象功能 鸭叫  呱呱
     */
    @Override
    public void quack() {
        if(honkable != null){
            log.info("adapter quack -> honk ");
            honkable.honk();
        }
    }
}



