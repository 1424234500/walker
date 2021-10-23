package com.walker.design.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理实现mybatis 结合注解 sql实现
 */
public class UserMapperInvocationHandler implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        try {
            if (method.getName().startsWith("get")) {
                return "name";
            } else if (method.getName().startsWith("set")) {
                return proxy;
            } else if (method.getName().startsWith("hello")) {
                return "world : implemnets by " + this.getClass().getSimpleName();
            } else if (method.getName().startsWith("add")) {
                return proxy;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
