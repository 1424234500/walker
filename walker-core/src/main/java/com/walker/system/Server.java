package com.walker.system;


import java.util.ArrayList;
import java.util.List;

/**
 * ssh服务器管理
 */
public class Server {
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

	public  Server addValues(Object values) {
		this.values.add(values);
		return this;
	}

	public Server(){

	}
	public Server(String group, String info, String name, String ip, String id, String pwd, String encode){
		this(ip, id, pwd);
		this.group = group;
		this.info = info;
		this.encode = encode;
		this.name = name;
	}

	public Server(String ip, String id, String pwd){
		this.ip = ip;
		this.id = id;
		this.pwd = pwd;
	}

	public String getInfo() {
		return info;
	}

	public Server setInfo(String info) {
		this.info = info;
		return this;
	}

	public String getName() {
		return name;
	}

	public Server setName(String name) {
		this.name = name;
		return this;
	}

	public String getGroup() {
		return group;
	}

	public Server setGroup(String group) {
		this.group = group;
		return this;
	}

	public String getIp() {
		return ip;
	}

	public Server setIp(String ip) {
		this.ip = ip;
		return this;
	}

	public String getId() {
		return id;
	}

	public Server setId(String id) {
		this.id = id;
		return this;
	}

	public String getPwd() {
		return pwd;
	}

	public Server setPwd(String pwd) {
		this.pwd = pwd;
		return this;
	}

	public String getEncode() {
		return encode;
	}

	public Server setEncode(String encode) {
		this.encode = encode;
		return this;
	}

	public String toSsh() {
		return this.getGroup() + "-" + this.getName() + " ssh " + id + "@" + ip + " ";// + " pwd: " + pwd + " ";
	}
	public String toSsh(String cmd ) {
		return this.getGroup() + "-" + this.getName() + " ssh " + id + "@" + ip + " " + cmd  + " pwd: " + pwd + " ";
	}
	@Override
	public String toString() {
		return "Server{" +
				"name='" + name + '\'' +
				", group='" + group + '\'' +
				", ip='" + ip + '\'' +
				", id='" + id + '\'' +
				", pwd='" + pwd + '\'' +
				", encode='" + encode + '\'' +
				'}';
	}
}
