package com.walker.socket.server.chat.plugin.aop;

import com.walker.core.mode.Bean;
import com.walker.socket.frame.SocketException;
import org.apache.log4j.Logger;

public abstract class Aop<MSG>{
	protected static Logger log = Logger.getLogger(Aop.class);

	Bean params;
	Aop(Bean params){
		this.params = params;
	}
	public void error(final Object...objects) throws SocketException {
		throw new SocketException(objects);
	}
	public abstract void doAop(MSG msg) throws SocketException;
}
