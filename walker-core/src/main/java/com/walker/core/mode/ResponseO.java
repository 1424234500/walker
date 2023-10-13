package com.walker.core.mode;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 标准响应结构
 */
@Data
@Accessors(chain = true)
public class ResponseO<T> extends Response<T>{
	long st = System.currentTimeMillis();

	long cost = 0;

	public String toInfo(){
		return super.toInfo() + "," + cost;
	}
	public void setCost(){
		this.cost = System.currentTimeMillis() - st;
	}

}
