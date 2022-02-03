package com.walker.socket.server.chat;

import com.walker.core.Context;
import com.walker.mode.Msg;
import com.walker.socket.server.chat.impl.ServerSocketNettyFileServer;
import org.apache.log4j.PropertyConfigurator;

public class LauncherFile {

	public static void main(String[] args) throws Exception {
		System.setProperty("path_conf", "conf");
		PropertyConfigurator.configure(Context.getPathConf("log4j.properties"));

		new ServerSocketNettyFileServer<Msg>(8080) .start();

	}

}
