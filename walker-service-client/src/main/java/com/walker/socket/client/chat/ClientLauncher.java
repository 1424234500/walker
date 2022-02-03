package com.walker.socket.client.chat;

import com.walker.core.Context;
import org.apache.log4j.PropertyConfigurator;


public class ClientLauncher {

	public static void main(String[] args) throws Exception {
		System.setProperty("path_conf", "conf");
		PropertyConfigurator.configure(Context.getPathConf("log4j.properties"));
		int p = 9070;

//        new ClientUI(new ClientNIO("127.0.0.1", p++), "nio-nio");
//		ClientNetty client = new ClientNetty("127.0.0.1", p++, "nio-netty");
//		client.setEncodeDecode(new DataEncodeDecodeJson<>(Msg.class));
//		new ClientUI(client);
//        new ClientUI(new ClientNIO("127.0.0.1", p++, "nio-netty"));
//        new ClientUI(new ClientNIO("127.0.0.1", p++), "nio-reactor");
//		try {
//			new ClientUI(new ClientNetty("10.31.29.104", Setting.get("socket_port_netty", 8093)), "netty-netty-client-39");
//		}catch (Exception e){
//			e.printStackTrace();
//		}

//		try {
//			new ClientUI(new ClientNIO("127.0.0.1", 8082), "nio-nio-client-127");
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//		try {
//			new ClientUI(new ClientAIO("127.0.0.1", 8083), "aio-aio-client-127");
//		}catch (Exception e){
//			e.printStackTrace();
//		}
		while (true) {
		}
	}

}
