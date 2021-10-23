package com.walker.design.factory;


import com.walker.design.template.Pizza;
import com.walker.design.template.PizzaCheese;
import com.walker.design.template.PizzaClam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 披萨店
 */
public abstract class PizzaStore {
    private static final Logger log = LoggerFactory.getLogger(PizzaStore.class);

    /**
     * 默认原料工厂newyork
     */
    public IngredientFactory getIngredientFactory() {
        return new IngredientFactoryNewyork();
    }

    public Pizza createPizza(String style) {
        Pizza pizza = null;

        IngredientFactory ingredientFactory = getIngredientFactory();
        switch (style) {
            case "clam":
                pizza = new PizzaClam(ingredientFactory);
                break;
            case "cheese":
            default:
                pizza = new PizzaCheese(ingredientFactory);
                break;
        }

        pizza.prepare();
        pizza.bake();
        pizza.box();


        log.info("createPizza ok " + style + " " + pizza);
        log.info("----------------------");
        return pizza;
    }


}



