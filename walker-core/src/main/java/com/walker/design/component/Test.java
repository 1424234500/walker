package com.walker.design.component;


import com.walker.design.iterator.MenuBreakfast;
import com.walker.design.iterator.MenuLaunch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {
    private static Logger log = LoggerFactory.getLogger(Test.class);

    public static void main(String[] argv){

        MenuLaunch<String> menuLaunch = new MenuLaunch<>().setItems("1", 2, 3, 4);
        MenuBreakfast<String> menuBreakfast = new MenuBreakfast<>().setItems("6", 7, 8);

        Component componentBreakfast = new ComponentMenu().setName("menu-breakfast")
                .add(new ComponentLeaf().setName("油条"))
                .add(new ComponentLeaf().setName("豆浆"))
                .add(
                        new ComponentMenu().setName("menu-西餐")
                                .add(new ComponentLeaf().setName("面包"))
                                .add(new ComponentLeaf().setName("牛奶"))
                )
                ;

        Component componentLaunch = new ComponentMenu().setName("menu-launch")
                .add(new ComponentLeaf().setName("回锅肉"))
                .add(new ComponentLeaf().setName("小炒肉"));
        Component componentDinner = new ComponentMenu().setName("menu-dinner")
                .add(new ComponentLeaf().setName("法式鹅肝"))
                .add(new ComponentMenu().setName("menu-美式"))
                .add(new ComponentMenu().setName("menu-中式")
                    .add(new ComponentLeaf().setName("稀饭"))
                )

                ;

        Component root = new ComponentMenu().setName("root-menu")
                .add(componentBreakfast)
                .add(componentLaunch)
                .add(componentDinner)
                ;

        Waitress<String> waitress = new Waitress<String>()
//                .setMenuBreakfasts(menuBreakfast.createIterator()).setMenuLaunch(menuLaunch.createIterator())
                .setComponent(root)
                ;

        waitress.printMenu();

    }



}



