package com.walker.socket.server.http;

import com.walker.core.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpJsonServerController {
	public Logger log = LoggerFactory.getLogger(getClass());
	String cr = "default";

	public HttpJsonServerController(){
	}
	public HttpJsonServerController(String cr){
		this.cr = cr;
	}

	public Map<String, Object> hello(String info){
		Map<String, Object> res = new LinkedHashMap<>();
		res.put("getFromClient-info", info);
		res.put("getFromClient-cr", cr);
		res.put("setToClient", TimeUtil.getTimeYmdHmss());
		return res;
	}
	public Map<String, Object> now(){
		Map<String, Object> res = new LinkedHashMap<>();
		res.put("getFromClient-cr", cr);
		res.put("setToClient", TimeUtil.getTimeYmdHmss());
		return res;
	}
	public String html(){
		return "<html><H1>time</H1><body><H3>" + TimeUtil.getTimeYmdHmss() + "</H3></body></html>";
	}
}