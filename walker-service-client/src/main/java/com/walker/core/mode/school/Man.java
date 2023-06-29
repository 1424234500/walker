package com.walker.core.mode.school;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 基本模型 用于测试 分库分表 walker0 walker1
 */

@Entity
@Table(name = "W_MAN")
public class Man implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	@Id     //主键
	@Column(name="ID", columnDefinition = "varchar(32) default '' comment '主键' ")
	private String id;
	@Column(name = "NAME", columnDefinition = "varchar(256) default '' comment '名字' ")    //255
    private String name;
	@Column(name = "SEX", columnDefinition = "varchar(4) default '' comment '性别' ")    //255
	private Integer sex;
	@Column(name = "PWD", columnDefinition = "varchar(32) default '' comment '密码' ")    //255
	private String pwd;
	@Column(name = "TIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
	private String time;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Integer getSex() {
		return sex;
	}

	public Man setSex(Integer sex) {
		this.sex = sex;
		return this;
	}

	@Override
	public String toString() {
		return "Man{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", sex='" + sex + '\'' +
				", pwd='" + pwd + '\'' +
				", time='" + time + '\'' +
				'}';
	}

	public String getId() {
		return id;
	}

	public Man setId(String id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Man setName(String name) {
		this.name = name;
		return this;
	}


	public String getPwd() {
		return pwd;
	}

	public Man setPwd(String pwd) {
		this.pwd = pwd;
		return this;
	}

	public String getTime() {
		return time;
	}

	public Man setTime(String time) {
		this.time = time;
		return this;
	}
}