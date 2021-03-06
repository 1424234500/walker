package com.walker.socket.server_1.plugin;

import com.walker.common.util.Bean;
import com.walker.mode.Msg;

public class EchoPlugin<T> extends Plugin<T>{

	EchoPlugin(Bean params) {
		super(params);
	}

	@Override
	public void onData(Msg msg) {
//		session.send(new Bean().set("plugin", "echo").set("params", params).set("data", msg)
//				.set("time", TimeUtil.getTimeYmdHmss()));
		msg.setStatus(1);
		msg.setInfo("from echo");
		publish(msg.getFrom(), msg);
	}

}
