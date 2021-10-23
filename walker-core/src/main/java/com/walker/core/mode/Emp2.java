package com.walker.core.mode;


/**
 * 基本对象 员工
 * @author Walker
 * 2017年10月30日09:50:08
 */
public class Emp2 extends Emp{
	
	public String id;   
	
	static{
		System.out.println("emp2 static{}");
	}
	
	public Emp2(){
		//super(); //默认自动调用父类空构造，可写可不写，若需要显示调用某个参数型的父构造
		System.out.println("emp2 init");
		
	}

	public String getId() {
		return id;
	}
	public Emp2 setId(String id) {
		this.id = id;
		super.id = id + "-super";
		return this;
	}
	
	public void fun(){
		System.out.println("emp2 fun this.id:" + this.id + " super.id:" + super.id);
	}

	@Override
	public String toString() {
		return "Emp2{" +
				"id='" + id + '\'' +
				'}';
	}
}
