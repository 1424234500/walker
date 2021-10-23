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
public class BeverageDecoratorMocha extends BeverageDecorator{
    @Override
    public float costAdd() {
        return 3 * beverage.getSizePoint();
    }
}
