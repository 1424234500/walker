package com.walker.socket.server.plugin.aop;

import com.walker.socket.base.Msg;
import com.walker.socket.base.impl.SocketException;
import com.walker.util.Bean;

public class AuthAop extends Aop<Msg> {

	AuthAop(Bean params) {
		super(params);
	}

	@Override
	public void doAop(Msg msg) throws SocketException {
		if(msg.getFromUser() == null) {
			String tip = this.params.get("tip", "") + msg.getData();
			log.warn(tip);
			error(tip);
		}else {
			log.info("已经登录");
		}
	}

}
