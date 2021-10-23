package com.walker.design.decorator;

/**
 *
 *
 * 装饰者模式        不改变接口，但是加入责任（新接口）
 *
 *              装饰者模式基础上使用父类 模板多态
 *              在被装饰的组件上 环绕组合或者替换覆盖(父类)
 *
 * 星巴兹咖啡 造价扩展问题
 *
 *
 * 买一杯（obj 底）
 *      咖啡
 *      奶茶
 *
 * 任意加（装饰）
 *      抹茶
 *      盐
 *      冰
 *
 * 给我总价
 * 小票清单
 *
 */
public abstract class Beverage {
    /**
     * 500ml
     */
    private int size = 500;

    /**
     * 超过额度 每 100ml 0.1倍溢价
     */
    public float getSizePoint(){
//        y = 1 + (x - 500) / 100 * 0.1f
        return 1 + Math.max((size - 500) / 100 * 0.1f, 0);
    }
    public int getSize() {
        return size;
    }

    public Beverage setSize(int size) {
        this.size = size;
        return this;
    }
    /**
     * 必须要有描述
     */
//    public abstract String description();
//    @Override
    public String description() {
        return cost() + "\tsize:" + getSize() + "\t" + this.getClass().getSimpleName();
    }

    /**
     * 基本饮料
     */
    public abstract float cost();



}
