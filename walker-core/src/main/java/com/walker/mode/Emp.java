package com.walker.mode;


import java.io.Serializable;

/**
 * 基本对象 员工 并测试继承多态static执行顺序
 * @author Walker
 * 2017年10月30日09:50:08
 */
public class Emp implements Serializable {
	
	public String id = "test";
	String name;
	String dept;

	static{
		System.out.println("emp static{}");
	}

	public Emp(){
		System.out.println("emp init");
	}

	@Override
	public String toString() {
		return "Emp{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", dept='" + dept + '\'' +
				'}';
	}

	public Emp(String id, String name, String dept) {
		this();
		this.id = id;
		this.name = name;
		this.dept = dept;
	}

	public void fun(){
		System.out.println("emp fun this.id:" + this.id  );
	}

	public String getId() {
		return id;
	}

	public Emp setId(String id) {
		this.id = id;
		return this;
	}
	public Emp setId(Object id) {
		this.id = String.valueOf(id);
		return this;
	}
	public String getName() {
		return name;
	}

	public Emp setName(String name) {
		this.name = name;
		return this;
	}

	public String getDept() {
		return dept;
	}

	public Emp setDept(String dept) {
		this.dept = dept;
		return this;
	}
}
