package com.walker.socket.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 消息表的实体W_MSG
 */
@Entity
@Table(name = "W_SHARDING_MSG")
public class Message implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	@Id     //主键
	@Column(name="ID", nullable = false, length = 32)
	private String id;
	@Column(name = "TEXT", nullable = false, columnDefinition = "TEXT")
    private String text;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getId() {
		return id;
	}

	public Message setId(String id) {
		this.id = id;
		return this;
	}

	public String getText() {
		return text;
	}

	public Message setText(String text) {
		this.text = text;
		return this;
	}

	@Override
	public String toString() {
		return "Message{" +
				"id='" + id + '\'' +
				", text='" + text + '\'' +
				'}';
	}
}