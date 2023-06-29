package com.walker.core.mode;


/**
 * 标准响应结构
 */
public class Response<T> {


	Boolean success = true;

	/**
	 * 标准错误码
	 */
	Error error;

	/**
	 * 额外提示 用于debug调试信息输出 额外透出内容
	 */
	String tip = "";
	/**
	 * 耗时
	 */
	Long cost = 0L;
	/**
	 * 结果
	 */
	T res;


	public String toInfo(){
		return "," + (success ? "success" : "error")
				+ (tip.isEmpty() ? "" : " ,tip" + tip)
				+ (error == null ? "" : ",level," + error.getLevel() + ",code," + error.getCode() + ",msg," + error.getMsg())
				+ (res == null ? "" : ",res," + res)
				;
	}

	public String getTip() {
		return tip;
	}

	public Response<T> setTip(String tip) {
		this.tip = tip;
		return this;
	}

	public Response<T> setCost(Long cost) {
		this.cost = cost;
		return this;
	}

	public long getCost() {
		return cost;
	}

	public Boolean getSuccess() {
		return success;
	}

	public Response<T> setSuccess(Boolean success) {
		this.success = success;
		return this;
	}

	public Error getError() {
		return error;
	}

	public Response<T> setError(Error error) {
		this.error = error;
		return this;
	}

	public T getRes() {
		return res;
	}

	public Response<T> setRes(T res) {
		this.res = res;
		return this;
	}
}
