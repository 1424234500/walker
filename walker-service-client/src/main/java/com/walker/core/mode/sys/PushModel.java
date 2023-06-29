package com.walker.core.mode.sys;

import com.walker.core.encode.JsonFastUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Map;

/**
 * 推送模型 队列缓冲
 */

@Entity
@Table(name = "W_PUSH_MODEL")
public class PushModel implements Cloneable, Serializable {
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

	@Column(name = "LEVEL", columnDefinition = "varchar(32) default '0' comment '优先级' ")    //255
	private String LEVEL;

	@Column(name = "USER_ID", columnDefinition = "varchar(32) default '' comment '目标用户id,分隔多个用户' ")    //255
	private String USER_ID;
	@Column(name = "TITLE", columnDefinition = "varchar(512) default 'title' comment '标题' ")    //255
	private String TITLE;
	@Column(name = "CONTENT", columnDefinition = "varchar(512) default 'content' comment '内容' ")    //255
	private String CONTENT;
	@Column(name = "TYPE", columnDefinition = "varchar(512) default '0' comment '类别 提醒|透传' ")    //255
	private String TYPE;
	@Column(name = "EXT", columnDefinition = "varchar(998) default '' comment '扩展参数' ")    //255
	private String EXT;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}


	@Override
	public String toString() {
		return "PushModel{" +
				"ID='" + ID + '\'' +
				", S_MTIME='" + S_MTIME + '\'' +
				", S_ATIME='" + S_ATIME + '\'' +
				", S_FLAG='" + S_FLAG + '\'' +
				", LEVEL='" + LEVEL + '\'' +
				", USER_ID='" + USER_ID + '\'' +
				", TITLE='" + TITLE + '\'' +
				", CONTENT='" + CONTENT + '\'' +
				", TYPE='" + TYPE + '\'' +
				", EXT='" + EXT + '\'' +
				'}';
	}

	public String getLEVEL() {
		return LEVEL;
	}

	public PushModel setLEVEL(String LEVEL) {
		this.LEVEL = LEVEL;
		return this;
	}

	public String getID() {
		return ID;
	}

	public PushModel setID(String ID) {
		this.ID = ID;
		return this;
	}

	public String getS_MTIME() {
		return S_MTIME;
	}

	public PushModel setS_MTIME(String s_MTIME) {
		S_MTIME = s_MTIME;
		return this;
	}

	public String getS_ATIME() {
		return S_ATIME;
	}

	public PushModel setS_ATIME(String s_ATIME) {
		S_ATIME = s_ATIME;
		return this;
	}

	public String getS_FLAG() {
		return S_FLAG;
	}

	public PushModel setS_FLAG(String s_FLAG) {
		S_FLAG = s_FLAG;
		return this;
	}

	public String getUSER_ID() {
		return USER_ID;
	}

	public PushModel setUSER_ID(String USER_ID) {
		this.USER_ID = USER_ID;
		return this;
	}

	public String getTITLE() {
		return TITLE;
	}

	public PushModel setTITLE(String TITLE) {
		this.TITLE = TITLE;
		return this;
	}

	public String getCONTENT() {
		return CONTENT;
	}

	public PushModel setCONTENT(String CONTENT) {
		this.CONTENT = CONTENT;
		return this;
	}

	public String getTYPE() {
		return TYPE;
	}

	public PushModel setTYPE(String TYPE) {
		this.TYPE = TYPE;
		return this;
	}

	public String getEXT() {
		return EXT;
	}
	public Map<String, String> getEXTObj() {
		Map<String, String> map = JsonFastUtil.get(EXT);
		return map;
	}


	public PushModel setEXT(String EXT) {
		this.EXT = EXT;
		return this;
	}
}