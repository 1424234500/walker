package com.walker.socket.server;

import com.walker.core.Context;
import com.walker.dubbo.DubboMgr;
import com.walker.mode.Msg;
import com.walker.socket.base.encode.DataEncodeDecodeJson;
import com.walker.socket.base.handler.HandlerEcho;
import com.walker.socket.base.handler.HandlerLog;
import com.walker.socket.base.handler.HandlerWrite;
import com.walker.socket.server.handler.HandlerSessionArpListImpl;
import com.walker.socket.server.impl.ServerSocketNetty;
import com.walker.socket.server.impl.ServerSocketNettyFileServer;
import org.apache.log4j.PropertyConfigurator;

import java.util.Arrays;

public class LauncherFile {

	public static void main(String[] args) throws Exception {
		System.setProperty("path_conf", "conf");
		PropertyConfigurator.configure(Context.getPathConf("log4j.properties"));

		new ServerSocketNettyFileServer<Msg>(8080) .start();

	}

}
