package com.walker.core.util;


import com.walker.core.encode.JsonUtil;
import com.walker.core.encode.SerializeUtil;
import com.walker.core.mode.Bean;

import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * 类控制 反转 缓存<?类加载机制自带缓存>
 * <p>
 * 策略 所有方法 可抛出 RuntimeException
 */

public class ClassUtil {
	private final static String defaultType = "String";

	public ClassUtil() {
//		out("private constructor");
	}

	private ClassUtil(String str) {
		out("private constructor str");
	}

	private final static void out(Object... objects) {
		System.out.println(Arrays.toString(objects));
	}

	/**
	 * 加载类
	 */
	public static Class<?> loadClass(String className) {
		Class<?> cls = null;
		try {
			cls = Thread.currentThread().getContextClassLoader().loadClass(className);
		} catch (ClassNotFoundException tcne) {
			try {
				cls = Class.forName(className);
			} catch (ClassNotFoundException cne) {
				throw new RuntimeException(Arrays.toString(new Object[]{"反射类", className, "加载异常", cne.toString()}));
			}
		}
		return cls;
	}

	/**
	 * 实例化 类
	 */
	public static Object newInstance(String className, Object... constructorArgs) {
		return newInstance(loadClass(className), constructorArgs);
	}

	/**
	 * 实例化 类
	 */
	public static Object newInstance(Class<?> cls, Object... constructorArgs) {
		Object res = null;
		try {
			Class<?>[] args = new Class[constructorArgs.length];
			for (int i = 0; i < constructorArgs.length; ++i) {
				args[i] = constructorArgs[i].getClass();
			}
			//		res = cls.newInstance(); //调用默认空构造<自动添加/手动编写>(不能是private!)

			Constructor<?> constructor = cls.getDeclaredConstructor(args);
			constructor.setAccessible(true);
			res = constructor.newInstance(constructorArgs);
		} catch (Exception e) {
			throw new RuntimeException("反射[" + cls + ".构造]" + Arrays.toString(constructorArgs) + e.getMessage());
		}
		return res;
	}

	/**
	 * 实例化方法
	 *
	 * @param cls
	 * @param methodName
	 */
	public static Method newMethod(Class<?> cls, String methodName, Object... methodArgs) {
		Class<?>[] args = new Class[methodArgs.length];
		for (int i = 0; i < methodArgs.length; ++i) {
			args[i] = methodArgs[i].getClass();
		}
		Method method = newMethod(cls, methodName, args);
		return method;
	}

	/**
	 * 实例化方法 指定类型 处理 简单类型 int.class
	 *
	 * @param cls
	 * @param methodName
	 */
	public static Method newMethod(Class<?> cls, String methodName, Class<?>... args) {
		Method method = null;
		try {
			method = cls.getMethod(methodName, args); //查询非自己private函数
		} catch (Exception noPrivateNsme) {
			out("反射 非私有private函数域没有这个方法 即将查找private区域  " + noPrivateNsme);
			try {
				//查询自己private函数
				method = cls.getDeclaredMethod(methodName, args);
			} catch (Exception e) {
				throw new RuntimeException("加载类" + cls + " 方法" + methodName + " 异常" + e, e);
			}
		}
		return method;
	}

	/**
	 * 简单构造 com.util.Tools.out
	 */
	public static Object doPackage(String packageName, List<ObjectClass> methodArgsEntry) {
		int index = packageName.lastIndexOf(".");
		String className = packageName.substring(0, index);
		String methodName = packageName.substring(index + 1);
		return new Builder(className).doMethod(methodName, methodArgsEntry.stream().map(item -> item.getCls()).toArray());
	}

	/**
	 * 调用 对象的目标方法
	 *
	 * @param instance
	 * @param method
	 * @param methodArgs
	 * @eg Object res = doInstanceMethod(instance, method, arg0);
	 */
	public static Object doInstanceMethod(Object instance, Method method, Object... methodArgs) {
		Object res = null;
		try {
			method.setAccessible(true);
			res = method.invoke(instance, methodArgs); //静态方法 instance 传 null 即可?
		} catch (Exception e) {
			throw new RuntimeException("反射" + "[" + instance.getClass().getName() + "." + method + " : " + Arrays.toString(methodArgs) + "]" + " 异常 " + e, e);
		}
		return res;
	}

	/**
	 * 注入代码段批量解析执行
	 * 词法解析 反射 缓存 临时变量
	 */
	public static Object doCode(List<String> list) {
		LinkedHashMap<String, Object> index = new LinkedHashMap<>();
		for (String line : list) {
//			list.addDataChildToNode("util.Bean bean = new util.Bean(); ");
//			list.addDataChildToNode("String str = new String(\"hello\"); ");
//			list.addDataChildToNode("bean.set(\"key\", str); ");
//			list.addDataChildToNode("Object res = bean.get(\"key\", \"default value\"); ");
//			list.addDataChildToNode("return res; ");
			//1.new Object()
			line = line.replace(";", "");
			line = line.replace("  ", " ");
			String args = "";
			Object[] argsObj = new Object[0];
			if (line.indexOf("(") >= 0 && line.indexOf(")") >= 0) {
				args = line.split("\\(", -1)[1].split("\\)", -1)[0]; // "hello", 22, 'c', 33L/D/F, false
				argsObj = parseObjectCodeArray(index, args, ",");
				line = line.substring(0, line.indexOf("("));
			}

			String[] param = line.split(" ");
			if (line.indexOf("return") >= 0) {
				//return bean;
				return index.get(param[1]);
			} else if (line.indexOf("new") > 0) {
				//String str = new String
				index.put(param[1], ClassUtil.newInstance(param[4], argsObj));
			} else if (line.indexOf("=") >= 0) {
				//Object res = bean.get
				//Object res = ClassUtil.do
				String key = param[3].substring(0, param[3].lastIndexOf("."));                // bean ClassUtil
				String method = param[3].substring(param[3].lastIndexOf(".") + 1);//get do
				Object obj = null;
				if (index.containsKey(key)) {
					obj = index.get(key);
					index.put(param[1], new ClassUtil.Builder(obj).doMethod(method, argsObj));
				} else {
					index.put(param[1], new ClassUtil.Builder(key).doMethod(method, argsObj));
				}
			} else {//bean.set
				//bean.get
				//ClassUtil.do
				String key = line.substring(0, line.lastIndexOf("."));// bean ClassUtil
				String method = line.substring(line.lastIndexOf(".") + 1);//get do
				Object obj = null;
				if (index.containsKey(key)) {
					obj = index.get(key);
					new ClassUtil.Builder(obj).doMethod(method, argsObj);
				} else {
					new ClassUtil.Builder(key).doMethod(method, argsObj);
				}
			}

		}

		Object[] keys = index.keySet().toArray();
		return index.get(keys[keys.length - 1]);
	}

	/**
	 * "hello", 2, 3L, false
	 */
	@SuppressWarnings("rawtypes")
	private static Object[] parseObjectCodeArray(Map index, String args, String splitArr) {
		List<Object> res = new ArrayList<>();
		splitArr = splitArr == null ? "," : splitArr;
		if (args != null && args.length() > 0) {
			args = args.replace(", ", ",");
			splitArr = splitArr == null ? "," : splitArr;
			String[] params = args.split(splitArr);
			for (String item : params) {
				String low = item.toUpperCase();
				if (item.startsWith("\"")) {
					res.add(item.replace("\"", ""));
				} else if (low.startsWith("'")) {
					res.add(item.charAt(1));
				} else if (low.equals("TRUE")) {
					res.add(Boolean.TRUE);
				} else if (low.equals("FALSE")) {
					res.add(Boolean.FALSE);
				} else if (item.charAt(0) <= '9' && item.charAt(0) >= '0') {
					if (low.indexOf("L") >= 0) {
						res.add(Long.valueOf(low));
					} else if (low.indexOf("D") >= 0) {
						res.add(Double.valueOf(low));
					} else if (low.indexOf("F") >= 0) {
						res.add(Float.valueOf(low));
					} else {
						res.add(Integer.valueOf(item));
					}
				} else {
					if (index.containsKey(item)) {
						res.add(index.get(item));
					} else {
						out("没有在上文找到[" + item + "]的定义！！！！！！！");
						res.add(null);
					}
				}

			}

		}
		return res.toArray();
	}

	/**
	 * 根据规则string获取对象列表
	 *
	 * @param args     : String-sss@Bean-{"k":"v"}@Integer-111@Boolean-true
	 * @param splitArr : @
	 * @param splitArg : -
	 *                 return [String(sss), Bean(k,v), Integer(111), Boolean(true)
	 */
	public static List<ObjectClass> parseObjectArray(String args, String splitArr, String splitArg) {
		List<ObjectClass> res = new ArrayList<>();
		splitArr = splitArr == null ? "@" : splitArr;
		splitArg = splitArg == null ? "-" : splitArg;

		if (splitArr == null || splitArr.length() == 0) {
			res.add(parseObject(defaultType, args));
		} else if (args == null) {
		} else {
			String[] arr = args.split(splitArr);
			if (splitArg == null || splitArg.length() == 0) {
				for (String item : arr) {
					res.add(parseObject(defaultType, item));
				}
			} else {
				for (String item : arr) {
					String[] typeValue = item.split(splitArg);
					if (typeValue.length > 1) {
						res.add(parseObject(typeValue[0], typeValue[1]));
					} else {
						res.add(parseObject(defaultType, item));
					}
				}
			}
		}

		return res;
	}

	/**
	 * 解析对象类型
	 *
	 * @param type  Integer string int
	 * @param value 123
	 * @return Integer(123)
	 */
	public static ObjectClass parseObject(String type, String value) {
		ObjectClass res = new ObjectClass("", String.class);
		if (type == null || type.length() == 0) {
			type = defaultType;
		}
		String typeL = type.toLowerCase();
		if (value != null) {
			try {
				if (typeL.equals("string")) {
					res.obj = value;
				}
				//包装类型
				else if (typeL.equals("integer")) {
					res.obj = new Integer(value);
				} else if (type.equals("Long")) {
					res.obj = Long.valueOf(value);
				} else if (type.equals("Double")) {
					res.obj = Double.valueOf(value);
				} else if (type.equals("Float")) {
					res.obj = Float.valueOf(value);
				} else if (type.equals("Boolean")) {
					res.obj = Boolean.valueOf(value);
				} else if (type.equals("Short")) {
					res.obj = Short.valueOf(value);
				} else if (type.equals("Byte")) {
					res.obj = Byte.valueOf(value);
				}
				//基本类型
				else if (typeL.equals("int")) {
					res.obj = new Integer(value).intValue();
					res.cls = int.class;
				} else if (type.equals("long")) {
					res.obj = Long.valueOf(value).longValue();
					res.cls = long.class;
				} else if (type.equals("double")) {
					res.obj = Double.valueOf(value).doubleValue();
					res.cls = double.class;
				} else if (type.equals("float")) {
					res.obj = Float.valueOf(value).floatValue();
					res.cls = float.class;
				} else if (type.equals("boolean")) {
					res.obj = Boolean.valueOf(value).booleanValue();
					res.cls = boolean.class;
				} else if (type.equals("short")) {
					res.obj = Short.valueOf(value).shortValue();
					res.cls = short.class;
				} else if (type.equals("byte")) {
					res.obj = Byte.valueOf(value).byteValue();
					res.cls = byte.class;
				}
				//常用对象类型
				else if (typeL.equals("hashmap")) { //无法实例化原生Map
					res.obj = new HashMap(JsonUtil.get(value));
				} else if (typeL.equals("map")) { //无法实例化原生Map ?
					res.obj = new HashMap(JsonUtil.get(value));
					res.cls = Map.class;
				} else if (typeL.equals("arraylist")) { //无法实例化原生List
					res.obj = new ArrayList(JsonUtil.get(value));
				} else if (typeL.equals("list")) { //无法实例化原生List
					res.obj = new ArrayList(JsonUtil.get(value));
					res.cls = List.class;
				} else if (typeL.equals("bean")) {
					res.obj = new Bean(JsonUtil.get(value));
				}
				//序列化传输 必须使用序列化 完整的str byte[]的数据  注意!!此处序列化会执行构造函数
				else if (type.equals("seria") || type.equals("serializeUtil")) {
					res.obj = SerializeUtil.deserialize(com.walker.core.encode.Base64.decode(value));
				}
				//反射 生成构造类对象 全路径 com.controller.Page-arg1,arg2 默认构造
				else {
					res.obj = newInstance(type);
				}
				res.cls = res.obj.getClass();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return res;
	}

	/**
	 * 获取某包下所有类 及其文件信息
	 *
	 * @param packageName  包名
	 * @param childPackage 是否遍历子包
	 * @return new Bean(FILE info, PACKAGE-类的完整名称)
	 */
	public static List<Bean> getPackageClassBean(String packageName, boolean childPackage) {
		List<Bean> fileNames = new ArrayList<>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String packagePath = packageName.replace(".", "/");
		//只能拿到本项目下的class文件/jar文件
		URL url = loader.getResource(packagePath);
		//jar:file:/E:/workspace_my/BaseSSM/WebContent/WEB-INF/lib/dom4j-1.6.1.jar!/org/dom4j
		//file:/E:/workspace_my/BaseSSM/WebContent/WEB-INF/classes/util
		if (url != null) {
			String type = url.getProtocol();
			if (type.equals("file")) {
				fileNames = getClassBeanByFile(url.getPath(), childPackage);
			} else if (type.equals("jar")) {
				fileNames = getClassBeanByJar(url.getPath(), childPackage);
			}
		} else {
//            fileNames = getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage);
		}
		return fileNames;
	}

	public static List<String> getPackageClass(String packageName, boolean childPackage) {
		List<String> fileNames = new ArrayList<>();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		String packagePath = packageName.replace(".", "/");
		//只能拿到本项目下的class文件/jar文件
		URL url = loader.getResource(packagePath);
		//jar:file:/E:/workspace_my/BaseSSM/WebContent/WEB-INF/lib/dom4j-1.6.1.jar!/org/dom4j
		//file:/E:/workspace_my/BaseSSM/WebContent/WEB-INF/classes/util
		if (url != null) {
			String type = url.getProtocol();
			if (type.equals("file")) {
				fileNames = getClassBeanByFile(url.getPath(), childPackage).stream().map(item -> item.get("PATH", "")).collect(Collectors.toList());
			} else if (type.equals("jar")) {
//                fileNames = getClassBeanByJar(url.getPath(), childPackage);
			}
		} else {
//            fileNames = getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage);
		}
		return fileNames;
	}

	/**
	 * 从项目文件获取某包下所有类
	 *
	 * @param filePath     文件路径
	 * @param childPackage 是否遍历子包
	 * @return 类的完整名称 及其 文件信息
	 */
	private static List<Bean> getClassBeanByFile(String filePath, boolean childPackage) {
		List<Bean> myClassName = new ArrayList<>();
		File file = new File(filePath);
		File[] childFiles = file.listFiles();
		for (File childFile : childFiles) {
			if (childFile.isDirectory()) {
				if (childPackage) {
					myClassName.addAll(getClassBeanByFile(childFile.getPath(), childPackage));
				}
			} else {
				String childFilePath = childFile.getPath();
				if (childFilePath.endsWith(".class")) {
					childFilePath = childFilePath.substring(childFilePath.indexOf("classes") + "classes/".length());
					childFilePath = childFilePath.substring(0, childFilePath.lastIndexOf("."));
					childFilePath = childFilePath.replace("\\", ".");
					myClassName.add(FileUtil.fileToMap(childFile).set("PACKAGE", childFilePath));
				}
			}
		}

		return myClassName;
	}

	/**
	 * 从项目文件获取某包下所有class 类全路径
	 *
	 * @param filePath     文件路径
	 * @param childPackage 是否遍历子包
	 * @return 类的完整名称
	 */
	private static List<String> getClassByFile(String filePath, boolean childPackage) {
		List<String> myClassName = new ArrayList<>();
		File file = new File(filePath);
		File[] childFiles = file.listFiles();
		for (File childFile : childFiles) {
			if (childFile.isDirectory()) {
				if (childPackage) {
					myClassName.addAll(getClassByFile(childFile.getPath(), childPackage));
				}
			} else {
				String childFilePath = childFile.getPath();
				if (childFilePath.endsWith(".class")) {
					childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9, childFilePath.lastIndexOf("."));
					childFilePath = childFilePath.replace("\\", ".");
					myClassName.add(childFilePath);
				}
			}
		}

		return myClassName;
	}

	/**
	 * 从jar获取某包下所有类
	 *
	 * @param jarPath      jar文件路径          //jar:file:/E:/workspace_my/BaseSSM/WebContent/WEB-INF/lib/dom4j-1.6.1.jar!/org/dom4j
	 * @param childPackage 是否遍历子包
	 * @return 类的完整名称 及其 文件信息
	 */
	private static List<Bean> getClassBeanByJar(String jarPath, boolean childPackage) {
		List<Bean> myClassName = new ArrayList<>();
		String[] jarInfo = jarPath.split("!");
		String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
		String packagePath = jarInfo[1].substring(1);
		try {
			@SuppressWarnings("resource")
			JarFile jarFile = new JarFile(jarFilePath);
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry jarEntry = entrys.nextElement();
				String entryName = jarEntry.getName();
				if (entryName.endsWith(".class")) {
					if (childPackage) {
						if (entryName.startsWith(packagePath)) {
							entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
							myClassName.add(new Bean().set("PACKAGE", entryName));
						}
					} else {
						int index = entryName.lastIndexOf("/");
						String myPackagePath;
						if (index != -1) {
							myPackagePath = entryName.substring(0, index);
						} else {
							myPackagePath = entryName;
						}
						if (myPackagePath.equals(packagePath)) {
							entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
							myClassName.add(new Bean().set("PACKAGE", entryName));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myClassName;
	}

	/**
	 * 获取包文件列表
	 *
	 * @param className 类路径 com.util.Bean
	 * @param ifArgs    是否显示成员变量
	 * @param ifFather  是否包括父级
	 * @return new Map(list,size)  <NAME,RETURNTYPE,PARAMETERTYPES,TOSTRING,BASE:0/1,TYPE:method/field>
	 */
	public static List<Bean> getMethod(String className, boolean ifArgs, boolean ifFather) {
		Class<?> cls = loadClass(className);
		List<Bean> res = new ArrayList<>();

		List<Method> methodAll = new ArrayList<>();//所有方法
		Collections.addAll(methodAll, cls.getMethods());

		List<Method> methodSelf = new ArrayList<>();//自有方法
		Collections.addAll(methodAll, cls.getDeclaredMethods());

		methodAll.removeAll(methodSelf); //余下的是基类的方法

		if (cls != null) {
			if (ifArgs) {//如果要变量 则先附加变量
				if (ifFather) { //显示父类 以及自己 的 所有方法 不要变量
					for (Field item : cls.getFields()) {
						Bean bean = turnField(className, item);
						res.add(bean);
					}
					//附加自己私有域
					for (Field item : cls.getDeclaredFields()) {
						if (getModifier(item).indexOf("private") >= 0) {
							res.add(turnField(className, item));
						}
					}
				} else {//只显示自己的
					for (Field item : cls.getDeclaredFields()) {
						Bean bean = turnField(className, item);
						res.add(bean);
					}
				}
			}
			if (ifFather) { //显示父类(仅public) 以及自己 的(不包括private)所有方法 不要变量
				for (Method item : cls.getMethods()) {
					Bean bean = turnMethod(className, item);
					res.add(bean);
				}
				//附加自己私有域
				for (Method item : cls.getDeclaredMethods()) {
					if (getModifier(item).indexOf("private") >= 0) {
						res.add(turnMethod(className, item));
					}
				}
			} else {//只显示自己的(所有的 包括private)
				for (Method item : cls.getDeclaredMethods()) {
					Bean bean = turnMethod(className, item);
					res.add(bean);
				}
			}
		}
		SortUtil.sort(res, "BASE");

		return res;
	}

	/**
	 * 获取包文件列表
	 * 类路径 util.Bean
	 * 是否显示成员变量
	 * 是否包括父级
	 * return new Map(list,size)  <NAME,RETURNTYPE,PARAMETERTYPES,TOSTRING,BASE:0/1,TYPE:method/field>
	 */
	public static List<Method> getMethodNative(String className, boolean ifFather) {
		Class<?> cls = loadClass(className);
		List<Method> res = new ArrayList<>();
		List<Method> methodAll = new ArrayList<>();//所有方法
		Collections.addAll(methodAll, cls.getMethods());
		List<Method> methodSelf = new ArrayList<>();//自有方法
		Collections.addAll(methodAll, cls.getDeclaredMethods());
		methodAll.removeAll(methodSelf); //余下的是基类的方法
		if (cls != null) {
			if (ifFather) { //显示父类(仅public) 以及自己 的(不包括private)所有方法 不要变量
				for (Method item : cls.getMethods()) {
					res.add(item);
				}
				//附加自己私有域
				for (Method item : cls.getDeclaredMethods()) {
					if (getModifier(item).indexOf("private") >= 0) {
						res.add(item);
					}
				}
			} else {//只显示自己的(所有的 包括private)
				for (Method item : cls.getDeclaredMethods()) {
					res.add(item);
				}
			}
		}
		return res;
	}

	/**
	 * 获取 Method 或者Field 的修饰符 public static final
	 */
	public static String getModifier(Member member) {
		int mod = member.getModifiers() & Modifier.methodModifiers();
		if (mod != 0) {
			return Modifier.toString(mod);
		}
		return "";
	}

	/**
	 * 获取 class 的修饰符 public static final
	 */
	public static String getModifier(Class<?> clz) {
		int mod = clz.getModifiers() & Modifier.classModifiers();
		if (mod != 0) {
			return Modifier.toString(mod);
		}
		return "";
	}

	private static Bean turnField(String className, Field item) {
		Bean bean = new Bean();
		//int aaa = 0;
		bean.put("NAME", item.getName()); //aaa
		bean.put("RETURNTYPE", item.getType());//int
		bean.put("PARAMETERTYPES", item.getType());
		bean.put("TOSTRING", filterString(className, item.toString()));
		bean.put("TYPE", "field");
		bean.put("MODIFIER", getModifier(item));//public static
		bean.put("BASE", item.getDeclaringClass().getName().equals(className) ? "self" : "base");
		return bean;
	}

	private static String filterString(String className, String str) {
		return str
				.replaceAll(className + ".", "")
				.replaceAll("java.lang.", "")
				.replaceAll("class ", "")
				.replaceAll("java.util.", "")
				.replaceAll("javax.servlet.http.", "")
				.replaceAll("interface ", "");
	}

	private static Bean turnMethod(String className, Method item) {
		Bean bean = new Bean();
		//int aaa = 0;
		bean.put("NAME", item.getName()); //aaa
		bean.put("RETURNTYPE", item.getReturnType());//int

		String str = filterString(className, Arrays.toString(item.getParameterTypes()));
		if (str.length() >= 2) {//[*, *]
			str = str.substring(1, str.length() - 1);
		}
		bean.put("PARAMETERTYPES", str.split(", "));

		bean.put("TOSTRING", filterString(className, item.toString()));
		bean.put("MODIFIER", getModifier(item));//public static
		bean.put("TYPE", "method");
		bean.put("BASE", item.getDeclaringClass().getName().equals(className) ? "self" : "base");
		return bean;
	}

	/**
	 * 通过反射,获得定义Class时声明的父类的范型参数的类型
	 *
	 * @param clazz
	 * @param index 返回某下标的类型 默认0
	 */
	public static Class getSuperClassGenricType(Class<?> clazz, int index)
			throws IndexOutOfBoundsException {
		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}
		return (Class) params[index];
	}

	public static void main(String[] argc) {
		out(new ClassUtil.Builder("ClassUtil").doMethod("testNoArgs"));
		out(new ClassUtil.Builder("ClassUtil").doMethodEntry("testInt", Arrays.asList(new ObjectClass(1, int.class))));
		out(new ClassUtil.Builder("ClassUtil").doMethod("testInteger", new Integer(1)));
		out(new ClassUtil.Builder("ClassUtil", "str").doMethod("testStr", "str"));
		out(new ClassUtil.Builder("ClassUtil", "str", 111).doMethod("testBean", new Bean().put("key", "vvv")));
//		out(new ClassUtil.Builder("ClassUtil").doMethod("testObjects", new String[]{"str", "str2"}));
		out(new ClassUtil.Builder("ClassUtil").doMethod("testNoReturn"));

		out(new ClassUtil.Builder("ClassUtil").doMethodEntry("test", ClassUtil.parseObjectArray("String-sss@Bean-{\"k\":\"v\"}@Integer-111@int-222@Boolean-true", "@", "-")));
		byte[] bb = SerializeUtil.serialize(new Bean().set("key", "value").set("key2", "value2"));
		String strSeria = new String(com.walker.core.encode.Base64.encode(bb));
		byte[] bt = com.walker.core.encode.Base64.decode(strSeria);
		out("seria", bb, strSeria);
		out("dseria byte", SerializeUtil.deserialize(bb));
		out("dseria str ", SerializeUtil.deserialize(bt));
		out(new ClassUtil.Builder("ClassUtil").doMethod("test", ClassUtil.parseObjectArray("String-sss@seria-"
				+ strSeria +
				"@Integer-111@Boolean-true", "@", "-")));

		out("testDocode", new ClassUtil.Builder("ClassUtil").doMethod("testDocode"));

//		Tools.formatOut(ClassUtil.getPackageClassBean("", true));
//		Tools.formatOut(ClassUtil.getMethodNative("com.mode.mode"));

//		Tools.formatOut(ClassUtil.getClassName("util", true));
//		Tools.formatOut(ClassUtil.getClassName("org.dom4j", true));
	}

	public String test(String str, Bean bean, Integer in, int int2, Boolean bool) {
		return Arrays.toString(new Object[]{"String", str, "bean", bean, "integer", in, "int", int2, "Boolean", bool});
	}


	////////////测试 test

	public String testNoArgs() {
		out();
		return "testNoArgs";
	}

	//int 不行?
	public String testInt(int i) {
		out(i);
		return "testInt";
	}

	public String testInteger(Integer i) {
		out(i);
		return "testInteger";
	}

	public String testStr(String i) {
		out(i);
		return "testStr";
	}

	@SuppressWarnings("unused")
	private String testBean(Bean i) {
		out(i);
		return "testBean";
	}

	//需要指定实际map类型
	public String testMap(HashMap<?, ?> i) {
		out(i);
		return "testMap";
	}

	public String testObjects(Object... objs) {
		out(objs.getClass());
		out(objs.length);
		out(objs);
		return "testObjects";
	}

	public void testNoReturn() {
		out("noReturn");
	}

	public Object testDocode() {
		List<String> list = new ArrayList<>();
		list.add("util.Bean bean = new util.Bean(); ");
		list.add("java.lang.String str = new java.lang.String(\"hello\"); ");
		list.add("bean.set(\"key\", str); ");
		list.add("Object res = bean.get(\"key\", \"default value\"); ");
		list.add("return res; ");
		Tools.formatOut(list);
		out(doCode(list));
		list.clear();
		list.add("util.Bean bean = new util.Bean(); ");
		list.add("bean.set(\"key1\", \"value1\")");
		list.add("util.Bean bean2 = new util.Bean(); ");
		list.add("bean2.set(\"key2\", bean); ");
		list.add("bean2.set(\"key3\", \"value3\"); ");
		list.add("return bean2; ");
		list.add("return bean1; ");
		Tools.formatOut(list);

		out(doCode(list));
		return "";
	}

	@SuppressWarnings("unchecked")
	public <T> T[] testCreate(T type) {
		return (T[]) Array.newInstance(type.getClass(), 10);
	}

	@SuppressWarnings("unchecked")
	public <T> T[] testCreate(Class<T> cls) {
		return (T[]) Array.newInstance(cls, 10);
	}

	//推断 协变类型  下转
	@SuppressWarnings("unchecked")
	public <T> T[] testCreate1(Class<?> cls) {
		return (T[]) Array.newInstance(cls, 10);
	}

	public static class ObjectClass {
		Object obj;
		Class<?> cls;

		public ObjectClass(Object obj, Class<?> cls) {
			this.obj = obj;
			this.cls = cls;
		}

		public Class<?> getCls() {
			return cls;
		}

		public Object getObj() {
			return obj;
		}
	}


	/**
	 * 类名
	 * 构造参数
	 * 方法名
	 * 方法参数
	 *
	 * 返回结果
	 */
	public static class Model{
		String className;
		List<Object> constructorArgs = new ArrayList<>();

		String methodName;
		List<Object> methodArgs = new ArrayList<>();

		public Object action(){
			return new Builder(className, constructorArgs.toArray()).doMethod(methodName, methodArgs.toArray());
		}

		public Model setClassName(String className) {
			this.className = className;
			return this;
		}

		public Model setConstructorArgs(Object...constructorArgs) {
			this.constructorArgs.clear();
			this.constructorArgs.addAll(Arrays.asList(constructorArgs));
			return this;
		}

		public Model setMethodName(String methodName) {
			this.methodName = methodName;
			return this;
		}

		public Model setMethodArgs(Object...methodArgs) {
			this.methodArgs.clear();
			this.methodArgs.addAll(Arrays.asList(methodArgs));
			return this;
		}

		public String getClassName() {
			return className;
		}

		public List<Object> getConstructorArgs() {
			return constructorArgs;
		}

		public String getMethodName() {
			return methodName;
		}

		public List<Object> getMethodArgs() {
			return methodArgs;
		}
	}


	public static class Builder {
		Object instance;

		public Builder(Object instance) {
			this.instance = instance;
		}

		public Builder(Class<?> cls, Object... constructorArgs) {
			this.instance = newInstance(cls, constructorArgs);
		}

		public Builder(String className, Object... constructorArgs) {
			this.instance = newInstance(loadClass(className), constructorArgs);
		}

		public Object doMethod(String methodName, Object... methodArgs) {
			return doMethod(newMethod(instance.getClass(), methodName, methodArgs), methodArgs);
		}

		public Object doMethodEntry(String methodName, List<ObjectClass> methodArgsEntry) {
			return doMethod(
					newMethod(instance.getClass(), methodName, methodArgsEntry.stream().map(item -> item.getCls()).collect(Collectors.toList()).toArray(new Class<?>[0]))
					, methodArgsEntry.stream().map(item -> item.getObj()).collect(Collectors.toList()).toArray());
		}

		public Object doMethod(Method method, Object... methodArgs) {
			return doInstanceMethod(this.instance, method, methodArgs);
		}
	}


}
  
