package com.walker.socket.server.chat;

import com.walker.core.Context;
import com.walker.dubbo.DubboMgr;
import com.walker.socket.model.Msg;
import com.walker.socket.encode.DataEncodeDecodeJson;
import com.walker.socket.handler.HandlerEcho;
import com.walker.socket.handler.HandlerLog;
import com.walker.socket.handler.HandlerWrite;
import com.walker.socket.server.chat.handler.HandlerSessionArpListImpl;
import com.walker.socket.server.chat.impl.ServerSocketNetty;
import org.apache.log4j.PropertyConfigurator;

import java.util.Arrays;

public class Launcher {

	public static void main(String[] args) throws Exception {
//		web项目需要配置于WEB-INF/classes 	spring.xml	 web.xml寻址classpath:   ? 
		System.setProperty("path_conf", "conf");
		PropertyConfigurator.configure(Context.getPathConf("log4j.properties"));

		new Thread(() -> {
			try {
				DubboMgr.getInstance().setDubboXml("dubbo-service-config.xml").start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		int p = 9070;

		new ServerSocketNetty<Msg>(p++)
				.setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class))
				.setHandlerChain(Arrays.asList(
						new HandlerLog<>()
						, new HandlerEcho<>()
						, HandlerSessionArpListImpl.getInstance()
						, new HandlerWrite<>()))
				.start();

	}

}
