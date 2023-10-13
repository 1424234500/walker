package com.walker.spring.component;

import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取spring上下文环境
 */

@Component
public class SpringContextUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext; // Spring应用上下文环境

	/**
	 * 将对象装换为map
	 *
	 * @param bean
	 */
	public static <T> Map<String, Object> beanToMap(T bean) {
		if (bean == null) return null;

		Map<String, Object> map = Maps.newHashMap();
		if (bean != null) {
			BeanMap beanMap = BeanMap.create(bean);
			for (Object key : beanMap.keySet()) {
				map.put(key + "", beanMap.get(key));
			}
		}
		return map;
	}

	public static <T> List<Map<String, Object>> beanToMap(List<T> list) {
		if (list == null) return null;

		List<Map<String, Object>> res = new ArrayList<>();
		for (T bean : list) {
			res.add(beanToMap(bean));
		}
		return res;
	}

	/**
	 * 将map装换为javabean对象
	 *
	 * @param map
	 * @param bean
	 */
	public static <T> T mapToBean(Map<String, Object> map, T bean) {
		if (bean == null) return null;

		BeanMap beanMap = BeanMap.create(bean);
		beanMap.putAll(map);
		return bean;
	}

	public static <T> List<T> mapToBean(List<Map<String, Object>> list, T bean) {
		if (list == null) return null;

		List<T> res = new ArrayList<>();
		for (Map<String, Object> map : list) {
			res.add(mapToBean(map, bean));
		}
		return res;
	}


	/**
	 * 不依赖于servlet,不需要注入
	 * Spring容器初始化时不可用！
	 */
	public static ApplicationContext getWebApplicationContext() {
		return null;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * ApplicationContextAware 接口方法；
	 * 通过传递applicationContext参数初始化成员变量applicationContext
	 * 环境初始化时自动注入？
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringContextUtil.applicationContext = applicationContext;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) throws BeansException {
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
	 *
	 * @return boolean
	 */
	public static boolean containsBean(String name) {
		return applicationContext.containsBean(name);
	}

	/**
	 * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
	 * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
	 *
	 * @return boolean
	 */
	public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return applicationContext.isSingleton(name);
	}

	/**
	 * @return Class 注册对象的类型
	 */
	public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		return applicationContext.getType(name);
	}

	/**
	 * 如果给定的bean名字在bean定义中有别名，则返回这些别名
	 */
	public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
		return applicationContext.getAliases(name);
	}
}
