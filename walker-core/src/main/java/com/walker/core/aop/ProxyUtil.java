package com.walker.core.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 *
 *
 *
 *
 * 动态代理
 * 拦截直接访问对象，可以给对象进行增强的一项技能
 */
public class ProxyUtil  {
	private static Logger log = LoggerFactory.getLogger(ProxyUtil.class);


	/**
	 * 动态代理实现 aop
	 * 所有接口监控执行前后环绕
	 * @param serviceImplInstance	接口实现类的实例
	 * @param <T>
	 * @return	接口实例
	 */
	public static <T> T getProxy(Object serviceImplInstance) {
		ClassLoader loader = serviceImplInstance.getClass().getClassLoader();   ///*类加载器*/
		Class<?>[] interfaces = serviceImplInstance.getClass().getInterfaces();    ///*让代理对象和目标对象实现相同接口*/
		Object serviceImplInstanceProxy = Proxy.newProxyInstance(loader, interfaces, new InvocationHandler() {
			/**
			 * 代理对象的接口的所有方法最终都会被JVM导向它的invoke方法 aop
			 */
			@Override
			public Object invoke(Object serviceImplInstanceProxy, Method method, Object[] methodArgs) throws Throwable {
				log.debug("invoke begin " + "[" + serviceImplInstance.getClass().getName() + "." + method + " : " + Arrays.toString(methodArgs) + "]" );
//				ClassUtil.doClassMethod(serviceImplInstance, method, methodArgs);
//				使用源对象实例唤醒方法
				Object result = method.invoke(serviceImplInstance, methodArgs);
				log.debug("invoke end   " + "[" + serviceImplInstance.getClass().getName() + "." + method + " : " + Arrays.toString(methodArgs) + "]" );
				return result;
			}
		});

		return (T) serviceImplInstanceProxy;
	}



	/**
	 * 动态代理实现 aop
	 * 所有接口监控执行前后环绕
	 * @param serviceImplInstance	接口实现类的实例
	 * @param <T>
	 * @return	接口实例
	 */
	public static <T> T getProxy(Object serviceImplInstance, InvocationHandler invocationHandler) {
		ClassLoader loader = serviceImplInstance.getClass().getClassLoader();   ///*类加载器*/
		Class<?>[] interfaces = serviceImplInstance.getClass().getInterfaces();    ///*让代理对象和目标对象实现相同接口*/
		Object serviceInstance = Proxy.newProxyInstance(loader, interfaces, invocationHandler);

		return (T) serviceInstance;
	}

	/**
	 * 动态代理实现 aop
	 * 接口代理
	 * @param clz 接口
	 * @return	接口实例
	 */
	public static <T> T getProxy(Class<T> clz, InvocationHandler invocationHandler) {
		ClassLoader loader = clz.getClassLoader();   ///*类加载器*/
		Class<?>[] interfaces = {clz}; //clz.getInterfaces();    ///*让代理对象和目标对象实现相同接口*/
		Object serviceInstance = Proxy.newProxyInstance(loader, interfaces, invocationHandler);

		return (T) serviceInstance;
	}



}