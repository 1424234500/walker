package com.walker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登录服务
 */
public interface LoginService  {
	Logger log = LoggerFactory.getLogger(LoginService.class);
	String CACHE_KEY = "cache-user-token";
	/**
	 * 默认 游客登录
	 */
	Boolean login();
	/**
	 * 指定id pwd登录
	 */
	Boolean saveLogin(String id, String pwd);
		
}