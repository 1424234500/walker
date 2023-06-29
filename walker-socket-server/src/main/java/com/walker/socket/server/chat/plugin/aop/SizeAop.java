package com.walker.socket.server.chat.plugin.aop;

import com.walker.core.mode.Bean;
import com.walker.socket.frame.SocketException;
import com.walker.socket.model.Msg;

public class SizeAop extends Aop<Msg> {
	
	SizeAop(Bean params) {
		super(params);
	}

	@Override
	public void doAop(Msg msg) throws SocketException {
		int length = msg.toString().length();
		int maxSize = this.params.get("size", 0);
		if(maxSize > 0 && length > maxSize ) {
			String tip = this.params.get("tip", "") + " " + maxSize + " " + msg.getData().toString().substring(0, 20);
			log.warn(tip);
			error(tip);
		}
	}

	
	
}
