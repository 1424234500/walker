package com.walker.design.proxy;


import java.lang.reflect.Proxy;

/**
 * 代理模式
 */
public class UserMapperFactory {


    public static UserMapper getOwnerUserMapper(UserMapper userMapper) {
        Class<?> clz = userMapper.getClass();
        return (UserMapper) Proxy.newProxyInstance(
                clz.getClassLoader()
                , clz.getInterfaces()
                , new OwnerInvocationHandler().setUserMapper(userMapper)
        );
    }

    public static UserMapper getNoOwnerUserMapper(UserMapper userMapper) {
//        Class<?> clz = UserMapper.class;
//        为什么上面不行呢 ？？？ UserMapper.class 和 userMapper.getClass() 的差别???
        Class<?> clz = userMapper.getClass();

        return (UserMapper) Proxy.newProxyInstance(
                clz.getClassLoader()
                , clz.getInterfaces()
                , new NoOwnerInvocationHandler().setUserMapper(userMapper)
        );
    }


    public static <T> T getMapper(Class<T> clz) {
        return (T) Proxy.newProxyInstance(
                clz.getClassLoader()
                , clz.getInterfaces()
                , new OwnerInvocationHandler()
        );
    }

}
