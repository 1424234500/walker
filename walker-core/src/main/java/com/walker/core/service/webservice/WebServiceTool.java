package com.walker.core.service.webservice;

import com.walker.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.ws.BindingProvider;
import java.util.Map;


public class WebServiceTool {
	private static final Logger log = LoggerFactory.getLogger("webservice");

	/**
	 * webservice超时设置
	 * 连接超时
	 * 写入超时
	 * 请求超时
	 * 分别应对兼容 sun jdk 和 ibm jdk
	 */
	public static <T> T webserviceConfig(T soap) {
		int connect = 2000;
		int write = 3000;
		int read = 5000;

		int connectSec = (int) Math.ceil(1.0 * connect / 1000);
		int writeSec = (int) Math.ceil(1.0 * write / 1000);
		int readSec = (int) Math.ceil(1.0 * read / 1000);
		try {
			Map<String, Object> map = ((BindingProvider) soap).getRequestContext();
			map.put("com.sun.xml.internal.ws.connect.timeout", connect);
			map.put("com.sun.xml.internal.ws.request.timeout", read);

//https://www.ibm.com/support/knowledgecenter/zh-tw/SS7JFU_8.5.5/com.ibm.websphere.nd.multiplatform.doc/ae/rwbs_jaxwstimeouts.html
//			Java constant name	Literal name	Default value
//			com.ibm.ws.websvcs.transport.common.TransportConstants.READ_TIMEOUT	readTimeout	300
//			com.ibm.ws.websvcs.transport.common.TransportConstants.WRITE_TIMEOUT	writeTimeout	300
//			com.ibm.ws.websvcs.transport.common.TransportConstants.CONN_TIMEOUT	connectTimeout	180
//			
//			Java constant name	Literal name	Default value
//			com.ibm.wsspi.webservices.Constants.RESPONSE_TIMEOUT_PROPERTY	timeout	300
//			com.ibm.wsspi.websvcs.Constants.WRITE_TIMEOUT_PROPERTY	write_timeout	300
//			com.ibm.wsspi.websvcs.Constants.CONNECTION_TIMEOUT_PROPERTY	connection_timeout	180
//			
//			Java constant name	Literal name	Default value
//			com.ibm.wsspi.webservices.Constants.RESPONSE_TIMEOUT_PROPERTY	timeout	300
//			com.ibm.wsspi.webservices.Constants.WRITE_TIMEOUT_PROPERTY	write_timeout	300
//			com.ibm.ws.websvcs.transport.http.WSHTTPConstants.HTTP_SOCKET_CONNECTION_TIMEOUT	com.ibm.websphere.webservices.http.SocketTimeout	180

			map.put("readTimeout", readSec);
			map.put("writeTimeout", writeSec);
			map.put("connectTimeout", connectSec);

			map.put("timeout", readSec + writeSec + connectSec);
			map.put("write_timeout", writeSec);
			map.put("connection_timeout", connectSec);

			map.put("timeout", readSec + writeSec + connectSec);
			map.put("write_timeout", writeSec);
			map.put("com.ibm.websphere.webservices.http.SocketTimeout", writeSec + connectSec);


		} catch (Exception e) {
			Tools.out(e.getMessage(), e);
		}
		return soap;
	}


}
