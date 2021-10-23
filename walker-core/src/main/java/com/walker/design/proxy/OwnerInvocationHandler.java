package com.walker.design.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 自己人代理 自能修改自己的属性 不能给自己拉票
 */
public class OwnerInvocationHandler implements InvocationHandler {

    UserMapper userMapper;

    public UserMapper getUserMapper() {
        return userMapper;
    }

    public OwnerInvocationHandler setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
        return this;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        try {
            if (method.getName().startsWith("get")
                    || method.getName().startsWith("set")) {
                return method.invoke(this.userMapper, args);
            } else if (method.getName().startsWith("hello")) {
                return "world : implemnets by " + this.getClass().getSimpleName();
            } else if (method.getName().startsWith("add")) {
                throw new IllegalAccessError("自己不能够给自己拉票");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
