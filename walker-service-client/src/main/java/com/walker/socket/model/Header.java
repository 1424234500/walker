package com.walker.socket.model;
import com.walker.core.annotation.DBSQLString;
import com.walker.mode.Property;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lilinfeng
 * @version 1.0
 * @date 2014年3月14日
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Header {

	private int crcCode = 0xabef0101;

	private int length;// 消息长度

	private long sessionID;// 会话ID

	private byte type;// 消息类型

	private byte priority;// 消息优先级

	private Map<String, Object> attachment = new HashMap<>(); // 附件


	public static class MessageType{
		public static final Property<Byte> HEARTBEAT_REQ = Property.build("HEARTBEAT_REQ", "心跳请求", "client发往server", (byte)(1 << 0));
		public static final Property<Byte> HEARTBEAT_RESP = Property.build("HEARTBEAT_RESP", "心跳回复", "server回答client", (byte)(1 << 1));
		public static final Property<Byte> LOGIN_REQ = Property.build("LOGIN_REQ", "登录请求", "client发往server", (byte)(1 << 2));
		public static final Property<Byte> LOGIN_RESP = Property.build("LOGIN_RESP", "登录回复", "", (byte)(1 << 3));
		public static final Property<Byte> HEARTBEAT_REQ1 = Property.build("HEARTBEAT_REQ", "心跳请求", "client发往server", (byte)(1 << 4));
	}



}
