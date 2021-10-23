package com.walker.design.decorator;

/**
 *
 *
 * 装饰者模式
 *
 * 装饰者模式        和父类做覆盖&组合操作 super.do + this.do
 *
 * is a 饮料
 *
 * 扩展
 *      咖啡
 *      啤酒
 *      奶茶
 *
 *
 */
public abstract class BeverageDecorator extends Beverage{



    public abstract float costAdd();


    Beverage beverage;

    public Beverage getBeverage() {
        return beverage;
    }

    public BeverageDecorator setBeverage(Beverage beverage) {
        this.beverage = beverage;
        this.setSize(beverage.getSize());
        return this;
    }

    /**
     *
     */
    @Override
    public float cost() {
        return this.costAdd() + beverage.cost();
    }

    /**
     * 模板
     */
    @Override
    public String description() {
        return beverage.description()
                + "\n"
                + costAdd() + "\t" + this.getClass().getSimpleName();    }



}
