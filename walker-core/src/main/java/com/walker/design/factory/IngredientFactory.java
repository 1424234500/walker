package com.walker.design.factory;


/**
 * 工厂模式        抽象工厂模式 (工厂基础上再抽象?)
 * <p>
 * 模板方法模式      抽象共同默认实现 抽象类
 * 钩子 子类具体影响父类的模板逻辑
 * 好莱坞原则   父类调用子类（抽象方法） 不要让子类中调用父类的方法？ 严格避免循环依赖!!
 * 定义算法骨架 一些步骤延迟到子类实现
 * <p>
 * 披萨店问题   类别扩展问题
 * pizzaStore
 * <p>
 * newyork      chicago
 * 每个城市都有两种款式的披萨
 * cheese  clam
 * 每个城市相同款式披萨原料不同
 * ingredientSauce     ingredientDough
 */
public interface IngredientFactory {
    IngredientSauce createSauce();

    IngredientDough createDough();


}
