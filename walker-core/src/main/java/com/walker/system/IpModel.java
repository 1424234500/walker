package com.walker.system;


import java.util.ArrayList;
import java.util.List;

/**
 * ssh服务器管理 基本元数据
 */
public class IpModel {
	String group;	//分组 xxx集群
	String info;	//说明

	String name;	//中文名
	String ip = "127.0.0.1";
	String id = "walker";
	String pwd = "";

	String encode = "utf-8";

	/**
	 *  返回值
	 */
	List<Object> values = new ArrayList<>();

	public <T> T getValue(int i){
		if(values.size() > i){
			return (T) values.get(i);
		}
		return null;
	}

	public List<Object> getValues() {
		return values;
	}

	public IpModel addValues(Object values) {
		this.values.add(values);
		return this;
	}

	public IpModel(){

	}

	public IpModel(String ip, String id, String pwd){
		this.ip = ip;
		this.id = id;
		this.pwd = pwd;
	}

	public String getInfo() {
		return info;
	}

	public IpModel setInfo(String info) {
		this.info = info;
		return this;
	}

	public String getName() {
		return name;
	}

	public IpModel setName(String name) {
		this.name = name;
		return this;
	}

	public String getGroup() {
		return group;
	}

	public IpModel setGroup(String group) {
		this.group = group;
		return this;
	}

	public String getIp() {
		return ip;
	}

	public IpModel setIp(String ip) {
		this.ip = ip;
		return this;
	}

	public String getId() {
		return id;
	}

	public IpModel setId(String id) {
		this.id = id;
		return this;
	}

	public String getPwd() {
		return pwd;
	}

	public IpModel setPwd(String pwd) {
		this.pwd = pwd;
		return this;
	}

	public String getEncode() {
		return encode;
	}

	public IpModel setEncode(String encode) {
		this.encode = encode;
		return this;
	}

	public String toSsh() {
		return this.getGroup() + "-" + this.getName() + " ssh " + id + "@" + ip + " ";// + " pwd: " + pwd + " ";
	}
}
