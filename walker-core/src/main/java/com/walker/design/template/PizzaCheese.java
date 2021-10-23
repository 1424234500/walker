package com.walker.design.template;


import com.walker.design.factory.IngredientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 奶酪披萨
 */
public class PizzaCheese extends Pizza{
    private static Logger log = LoggerFactory.getLogger(PizzaCheese.class);

    IngredientFactory ingredientFactory;

    /**
     * 把需要的具体实现封到factory中传递进来
     * @param ingredientFactory
     */
    public PizzaCheese(IngredientFactory ingredientFactory){
        this.ingredientFactory = ingredientFactory;
    }


    @Override
    public void prepare() {
        this.setName(getClass().getSimpleName());
//        this.setDough("Thin Crust Dough");
//        this.setSauce("More Sauce");
        this.addToppings("Grated Cheese");

        this.setIngredientDough(this.ingredientFactory.createDough());
        this.setIngredientSauce(this.ingredientFactory.createSauce());

        super.prepare();

    }

    @Override
    public boolean isCutable() {
        return true;
    }
}



