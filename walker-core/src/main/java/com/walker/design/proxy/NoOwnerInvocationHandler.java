package com.walker.design.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 他人代理 不能修改属性 能给自己拉票
 */
public class NoOwnerInvocationHandler implements InvocationHandler {

    UserMapper userMapper;

    public UserMapper getUserMapper() {
        return userMapper;
    }

    public NoOwnerInvocationHandler setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
        return this;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        try {
            if (method.getName().startsWith("get")
                    || method.getName().startsWith("add")) {
                return method.invoke(this.userMapper, args);
            } else if (method.getName().startsWith("hello")) {
                return "world : implemnets by " + this.getClass().getSimpleName();
            } else if (method.getName().startsWith("set")) {
                throw new IllegalAccessError("自己不能够给自己拉票");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
