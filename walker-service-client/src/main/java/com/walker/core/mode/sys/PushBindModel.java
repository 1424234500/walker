package com.walker.core.mode.sys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 推送绑定
 *
 * 存储结构
 *
 * redis	set
 * key_${user_id1}
 * 		${push_id1}, ${type1}
 * 		${push_id2}, ${type2}
 *
 * key_${user_id2}
 * 		${push_id1}, ${type1}
 * 		${push_id2}, ${type2}
 *
 *
 *
 * mysql
 *
 */

@Entity
@Table(name = "W_PUSH_BIND_MODEL")
public class PushBindModel implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	@Id     //主键
	@Column(name="ID", columnDefinition = "varchar(32) default '' comment '主键' ")
	private String ID;
	@Column(name = "S_MTIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
	private String S_MTIME;
	@Column(name = "S_ATIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '添加时间' ")
	private String S_ATIME;
	@Column(name = "S_FLAG", columnDefinition = "varchar(4) default '0' comment '1是0否' ")
	private String S_FLAG;

	@Column(name = "USER_ID", columnDefinition = "varchar(32) default '' comment '用户id' ")    //255
	private String USER_ID;
	@Column(name = "PUSH_ID", columnDefinition = "varchar(512) default '' comment '推送id' ")    //255
	private String PUSH_ID;
	@Column(name = "DEVICE_ID", columnDefinition = "varchar(512) default '' comment '设备id' ")    //255
	private String DEVICE_ID;
	@Column(name = "TYPE", columnDefinition = "varchar(512) default '0' comment '推送:极光|华为|小米' ")    //255
	private String TYPE;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "PushBindModel{" +
				"ID='" + ID + '\'' +
				", S_MTIME='" + S_MTIME + '\'' +
				", S_ATIME='" + S_ATIME + '\'' +
				", S_FLAG='" + S_FLAG + '\'' +
				", USER_ID='" + USER_ID + '\'' +
				", PUSH_ID='" + PUSH_ID + '\'' +
				", DEVICE_ID='" + DEVICE_ID + '\'' +
				", TYPE='" + TYPE + '\'' +
				'}';
	}

	public String getDEVICE_ID() {
		return DEVICE_ID;
	}

	public PushBindModel setDEVICE_ID(String DEVICE_ID) {
		this.DEVICE_ID = DEVICE_ID;
		return this;
	}

	public String getID() {
		return ID;
	}

	public PushBindModel setID(String ID) {
		this.ID = ID;
		return this;
	}

	public String getS_MTIME() {
		return S_MTIME;
	}

	public PushBindModel setS_MTIME(String s_MTIME) {
		S_MTIME = s_MTIME;
		return this;
	}

	public String getS_ATIME() {
		return S_ATIME;
	}

	public PushBindModel setS_ATIME(String s_ATIME) {
		S_ATIME = s_ATIME;
		return this;
	}

	public String getS_FLAG() {
		return S_FLAG;
	}

	public PushBindModel setS_FLAG(String s_FLAG) {
		S_FLAG = s_FLAG;
		return this;
	}

	public String getUSER_ID() {
		return USER_ID;
	}

	public PushBindModel setUSER_ID(String USER_ID) {
		this.USER_ID = USER_ID;
		return this;
	}

	public String getPUSH_ID() {
		return PUSH_ID;
	}

	public PushBindModel setPUSH_ID(String PUSH_ID) {
		this.PUSH_ID = PUSH_ID;
		return this;
	}

	public String getTYPE() {
		return TYPE;
	}

	public PushBindModel setTYPE(String TYPE) {
		this.TYPE = TYPE;
		return this;
	}
}