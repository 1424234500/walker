package com.walker.socket.model;

import javax.persistence.*;
import java.io.Serializable;


/**
 * 消息表的实体W_MSG_USER
 */
@Entity
@IdClass(MessageUserPK.class)
@Table(name = "W_SHARDING_MSG_USER")
public class MessageUser implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	@Id     //主键
	@Column(name="ID", nullable = false, length = 200)
	private String id;
	@Id		//主键
	@Column(name="MSG_ID", nullable = false, length = 40)
	private String msgId;

	@Column(name="USER_FROM", nullable = false, length = 40)
	private String userFrom;
	@Column(name="USER_TO", nullable = false, length = 40)
	private String userTo;
	@Column(name="S_MTIME", nullable = false, length = 32)
	private String smtime;

	@Override
	public String toString() {
		return "MessageUser{" +
				"id='" + id + '\'' +
				", userFrom='" + userFrom + '\'' +
				", userTo='" + userTo + '\'' +
				", msgId='" + msgId + '\'' +
				", smtime='" + smtime + '\'' +
				'}';
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getId() {
		return id;
	}

	public MessageUser setId(String id) {
		this.id = id;
		return this;
	}

	public String getUserFrom() {
		return userFrom;
	}

	public MessageUser setUserFrom(String userFrom) {
		this.userFrom = userFrom;
		return this;
	}

	public String getUserTo() {
		return userTo;
	}

	public MessageUser setUserTo(String userTo) {
		this.userTo = userTo;
		return this;
	}

	public String getMsgId() {
		return msgId;
	}

	public MessageUser setMsgId(String msgId) {
		this.msgId = msgId;
		return this;
	}

	public String getSmtime() {
		return smtime;
	}

	public MessageUser setSmtime(String smtime) {
		this.smtime = smtime;
		return this;
	}
}