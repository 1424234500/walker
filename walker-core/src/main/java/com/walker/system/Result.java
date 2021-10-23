package com.walker.system;


import com.walker.util.Constant;

/**
 * 结果封装
 */
public class Result {

	String key = ""; //cmd key
	long timeStart = System.currentTimeMillis();
	long cost = 0;
	int isOk = Constant.OK;	//0否 1是
	Object res = null;
	String info = "";	//说明

	@Override
	public String toString() {
		return "" + key
				+ " isOk: " + String.valueOf(isOk)
				+ " cost: " + String.valueOf(cost)
				+ " res: " + String.valueOf(res)
				+ " info: " + String.valueOf(info)
				;
	}
	public String toRes() {
		String s = "";
		if(this.getIsOk() == Constant.OK){
			s += res;
		}else{
			s += this.info;
		}
		return s;
	}
	public String getKey() {
		return key;
	}

	public Result setKey(String key) {
		this.key = key;
		return this;
	}

	public long getTimeStart() {
		return timeStart;
	}

	public Result setTimeStart(long timeStart) {
		this.timeStart = timeStart;
		return this;
	}

	public long getCost() {
		return cost;
	}

	public Result setCost(long cost) {
		this.cost = cost;
		return this;
	}

	public int getIsOk() {
		return isOk;
	}

	public Result setIsOk(int isOk) {
		this.isOk = isOk;
		return this;
	}

	public <T> T getRes() {
		return (T) res;
	}

	public Result setRes(Object res) {
		this.res = res;
		return this;
	}

	public String getInfo() {
		return info;
	}

	public Result setInfo(String info) {
		this.info = info;
		return this;
	}
	public Result addInfo(String info) {
		this.info += info;
		return this;
	}
}
