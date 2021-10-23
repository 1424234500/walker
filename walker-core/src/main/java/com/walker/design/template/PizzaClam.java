package com.walker.design.template;


import com.walker.design.factory.IngredientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 蛤披萨
 */
public class PizzaClam extends Pizza {
    private static final Logger log = LoggerFactory.getLogger(PizzaClam.class);


    IngredientFactory ingredientFactory;

    /**
     * 把需要的具体实现封到factory中传递进来
     *
     * @param ingredientFactory
     */
    public PizzaClam(IngredientFactory ingredientFactory) {
        this.ingredientFactory = ingredientFactory;
    }


    @Override
    public void prepare() {
        this.setName(getClass().getSimpleName());
//        this.setDough("Extra Crust Dough");
//        this.setSauce("Plum Tomato Sauce");
        this.addToppings("Less Cheese");


        this.setIngredientDough(this.ingredientFactory.createDough());
        this.setIngredientSauce(this.ingredientFactory.createSauce());
        super.prepare();
    }

    public void cut() {
        log.info("Cut " + name + " 998");
    }

    @Override
    public boolean isCutable() {
        return false;
    }

}




