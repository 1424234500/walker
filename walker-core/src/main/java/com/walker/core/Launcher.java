package com.walker.core;

import com.walker.util.ThreadUtil;
import com.walker.util.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Launcher {

	public static void main(String[] args) {
		new Launcher();
	}

	
	public Launcher() {
//		web项目需要配置于WEB-INF/classes 	spring.xml	 web.xml寻址classpath:   ? 
//		PropertyConfigurator.configure(Context.getPathConf("log4j.properties"));


		Tools.out("-----------------launcher-------------------");
		String root = Context.getPathConf();
		Tools.out(root);
		Tools.formatOut( new File(root).list());

//		test1();
//		testAnd();
		testSplit();
		testArgs();
		testArgs(null);
		testNull();

		Tools.out("-----------------end-------------------");
		ThreadUtil.sleep(10 * 1000);
	}

	void testArgs(Object...objects){
		Tools.out("null?", objects == null);
		if(objects != null)
		Tools.out("length", objects.length);
	}
	void testNull(){

		Object obj = null;
		System.out.println(String.valueOf(obj));
		try {
			System.out.println(obj.toString());
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public void testSplit() {
		Tools.out(",a,b".split(",")[1]);
	}
	public void testAnd() {
		int max = 10;
		
		for(int i = 0; i < max; i++) {
			byte c = (byte) i;
			Tools.out(c);
			List<Object> list = new ArrayList<>();
			for(int j = 0; j < 8; j++) {
				list.add(c & j);
			}
			Tools.out(list);
			
		}
		
		
		
	}
	public void test1() {

		int a = 1;
		Integer b = new Integer(1);
		Integer c = a;
		int d = b;
		fun(a, b, c, d, b.intValue());
	}
	public void fun(Object...methodArgs) {
		Class<?>[] args = new Class[methodArgs.length];
		for (int i = 0; i < methodArgs.length; ++i) {
			args[i] = methodArgs[i].getClass();
			Tools.out(args[i]);
		}
		
	
	}

}
