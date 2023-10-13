package com.walker.core.mode;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 标准响应结构
 */
@Data
@Accessors(chain = true)
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

	long total = -1;
	/**
	 * 结果
	 */
	T res;


	public String toInfo(){
		return "," + (success ? "success" : "error")
				+ (tip.isEmpty() ? "" : " ,tip" + tip)
				+ (error == null ? "" : ",level," + error.getLevel() + ",code," + error.getCode() + ",msg," + error.getMsg())
				+ (total != -1 ? "" : ",total," + total)
				+ (res == null ? "" : ",res," + res)
				;
	}

	public Response<T> setError(Error error){
		this.error = error;
		success = false;
		return this;
	}
}
