package com.walker.core.annotation;

import com.walker.core.database.BaseDao;
import com.walker.core.database.SqlUtil;
import com.walker.core.mode.Page;
import com.walker.util.Tools;

import java.lang.annotation.AnnotationFormatError;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 动态代理实现指定注解的类的接口 生成实例对象
 */

public class WalkerJdbcProxy implements InvocationHandler {
	private BaseDao baseDao;

	public BaseDao getBaseDao() {
		return baseDao;
	}

	public WalkerJdbcProxy setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
		return this;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//不需要处理的方法交由其他处理
		try {
			if (Object.class.equals(method.getDeclaringClass())) {
				return method.invoke(this, args);
			}

			if (this.isDefaultMethod(method)) {
				return this.invokeDefaultMethod(proxy, method, args);
			}
		} catch (Throwable var5) {
			throw var5;
		}
		WalkerJdbcQuery walkerJdbcQuery = method.getAnnotation(WalkerJdbcQuery.class);
		if (walkerJdbcQuery == null) {
			throw new AnnotationFormatError("no the annotation WalkerJdbcQuery ");
		}
		String name = walkerJdbcQuery.name();
		String sqlValue = walkerJdbcQuery.value();
		if (sqlValue.length() == 0) {
			throw new AnnotationFormatError("err sqlValue name null " + method);
		}
		Tools.out("proxy " + method + " sql:" + sqlValue + " args:" + args);
		return executeSql(sqlValue, method, args);
	}

	//		/**
//		 * sql执行
//		 * @param id
//		 * @param name
//		 * @return 操作数量
//		 */
//		@WalkerJdbcQuery("update STUDENT set NAME=? where ID=? ")
//		Integer execute(String name, String id);
	private Object executeSql(String sql, Method method, Object[] args) {
		if (SqlUtil.isSelect(sql)) {
			if (args.length <= 0 || !(args[0] instanceof Page)) {
				throw new NoSuchElementException("select args 0 must be page ");
			}
			List<Object> list = new ArrayList<>();
			list.addAll(Arrays.asList(args));

			Page page = (Page) list.get(0);
			list.remove(0);

			List<Map<String, Object>> res = baseDao.findPage(page, sql, list.toArray());

//			return LangUtil.turnMap2ObjList(res, cls);
			return res;

		} else {
			return baseDao.executeSql(sql, args);
		}
	}

	private boolean isDefaultMethod(Method method) {
		return (method.getModifiers() & 1033) == 1 && method.getDeclaringClass().isInterface();
	}

	//	@UsesJava7
	private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
		Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
		if (!constructor.isAccessible()) {
			constructor.setAccessible(true);
		}

		Class<?> declaringClass = method.getDeclaringClass();
		return constructor.newInstance(declaringClass, 15).unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
	}

}




