package com.walker.core;

import com.walker.core.util.ThreadUtil;
import com.walker.core.util.Tools;

import java.io.File;

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
		Tools.out("-----------------end-------------------");
		ThreadUtil.sleep(10 * 1000);
	}


}
