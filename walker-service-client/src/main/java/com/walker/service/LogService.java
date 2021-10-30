package com.walker.service;

import com.walker.mode.LogModel;
import com.walker.mode.LogSocketModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志管理
 */
public interface LogService  {
	Logger log = LoggerFactory.getLogger(LogService.class);
	String CACHE_KEY = "cache-url-request";
	String CACHE_KEY_CONTROL = "cache-url-request-control";

	/**
	 * 记录临时日志 用于状态机 任务执行中 无耗时  再次主键覆盖才计时 计成功率 立即存储
	 */
	LogModel saveLogModelNoTime(LogModel logModel);

	/**
	 * 日志记录模型 controller job	缓存存储
	 */
	LogModel saveLogModel(LogModel logModel);

	/**
	 * 把缓冲的日志记录和 日志统计结果记录 持久化
	 */
	void saveStatis();




	/**
	 * 记录socket日志
	 */
	LogSocketModel saveLogSocketModel(LogSocketModel logModel);




}