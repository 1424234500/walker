package com.walker.design.template;


import com.walker.design.factory.IngredientDough;
import com.walker.design.factory.IngredientSauce;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *  模板方法模式      抽象共同默认实现 抽象类
 *      钩子 子类具体影响父类的模板逻辑
 *      好莱坞原则   父类调用子类（抽象方法） 不要让子类中调用父类的方法？ 严格避免循环依赖!!
 *      定义算法骨架 一些步骤延迟到子类实现
 *
 *  披萨店问题   类别扩展问题
 *  pizzaStore
 *
 * newyork      chicago
 * 每个城市都有两种款式的披萨
 * cheese  clam
 * 每个城市相同款式披萨原料不同
 * ingredientSauce     ingredientDough
 *
 *
 *
 */
public abstract class Pizza {
    private static Logger log = LoggerFactory.getLogger(Pizza.class);

    /**
     * 名字
     */
    String name;
    /**
     *使用什么面团
     */
    IngredientDough ingredientDough;
    /**
     * 加多少盐
     */
    IngredientSauce ingredientSauce;

    /**
     * 额外操作
     */
    List<String> toppings = new ArrayList<>();

    @Override
    public String toString() {
        return "Pizza{" +
                "name='" + name + '\'' +
                ", ingredientDough=" + ingredientDough +
                ", ingredientSauce=" + ingredientSauce +
                '}';
    }

    //    abstract void prepare();
    public void prepare(){
        log.info("prepare " + this.toString());
        for(String topp : toppings){
            log.info("\t" + topp);
        }
    }
    public void bake(){
        log.info("bake " + name + "");
        if(isCutable()){
            cut();
        }
    }
    public void cut(){
        log.info("cut " + name + "");
    }
    public void box(){
        log.info("box " + name + "");
    }

    /**
     * 钩子决定模板逻辑分支改变
     */
    abstract boolean isCutable();

    public String getName() {
        return name;
    }

    public Pizza setName(String name) {
        this.name = name;
        return this;
    }

    public IngredientDough getIngredientDough() {
        return ingredientDough;
    }

    public Pizza setIngredientDough(IngredientDough ingredientDough) {
        this.ingredientDough = ingredientDough;
        return this;
    }

    public IngredientSauce getIngredientSauce() {
        return ingredientSauce;
    }

    public Pizza setIngredientSauce(IngredientSauce ingredientSauce) {
        this.ingredientSauce = ingredientSauce;
        return this;
    }

    public List<String> getToppings() {
        return toppings;
    }

    public Pizza setToppings(List<String> toppings) {
        this.toppings = toppings;
        return this;
    }

    public Pizza addToppings(String topping) {
        this.toppings.add(topping);
        return this;
    }

}



