package com.walker.mode.sys;

import com.walker.util.TimeUtil;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 监控模型 单次接口
 *
 * 分类	CATE
 *
 * 哪个用户	USER
 * 从哪个主机	IP_PORT_FROM
 * 什么时间	S_MTIME
 * 访问了什么东西	URL
 * 如何访问	WAY
 * 访问的目标服务器	IP_PORT_TO
 * 带了什么参数	ARGS
 * 是否抛出异常	IS_EXCEPTION
 * 抛出了什么异常	EXCEPTION
 * 是否访问成功	IS_OK
 * 访问的结果	RES
 * 消耗了多少时间	COST
 *
 * 其他说明	ABOUT
 *
 *
 */

@Entity
@Table(name = "W_LOG_DUBBO_INVOKE"
		, indexes = {
//                @Index(name = "INDEX_W_AREA_PATH", columnList = "PATH"),      //索引不能超过1000
//                @Index(name = "INDEX_W_AREA_PATH_NAME", columnList = "PATH_NAME"),
		@Index(name = "INDEX_W_LOG_DUBBO_INVOKE_SERVICE", columnList = "SERVICE")
		, @Index(name = "INDEX_W_LOG_DUBBO_INVOKE_METHOD", columnList = "METHOD")
}
)

public class DubboInvoke implements Cloneable, Serializable {
	@Id     //主键
	@Column(name="ID", columnDefinition = "varchar(32) default '' comment '主键' ")
	private String ID;
	@Column(name = "INVOKE_DATE", columnDefinition = "varchar(32) default '' comment '修改时间' ")
	private String INVOKE_DATE = TimeUtil.getTimeYmdHmss();
	@Column(name = "INVOKE_TIME", columnDefinition = "varchar(32) default '' comment '调用时间' ")
	private String INVOKE_TIME = TimeUtil.getTimeYmdHmss();

	@Column(name = "SERVICE", columnDefinition = "varchar(128) default '' comment '服务' ")    //255
	private String SERVICE;
	@Column(name = "METHOD", columnDefinition = "varchar(128) default '' comment '方法' ")
    private String METHOD;

	@Column(name = "CONSUMER", columnDefinition = "varchar(32) default '' comment '消费' ")
	private String CONSUMER;
	@Column(name = "PROVIDER", columnDefinition = "varchar(32) default '' comment '提供' ")
	private String PROVIDER;
	@Column(name = "TYPE", columnDefinition = "varchar(32) default '' comment '类型' ")
	private String TYPE;

	@Column(name = "SUCCESS", columnDefinition = "varchar(32) default '' comment '成功' ")
	private String SUCCESS;
	@Column(name = "FAILURE", columnDefinition = "varchar(32) default '' comment '失败' ")
	private String FAILURE;

	@Column(name = "ELAPSED", columnDefinition = "varchar(32) default '' comment '耗时' ")
	private String ELAPSED;
	@Column(name = "MAX_ELAPSED", columnDefinition = "varchar(32) default '' comment '最大耗时' ")
	private String MAX_ELAPSED;


	@Column(name = "CONCURRENT", columnDefinition = "varchar(32) default '' comment '并发' ")
	private String CONCURRENT;
	@Column(name = "MAX_CONCURRENT", columnDefinition = "varchar(32) default '' comment '最大并发' ")
	private String MAX_CONCURRENT;


	public String getID() {
		return ID;
	}

	public DubboInvoke setID(String ID) {
		this.ID = ID;
		return this;
	}

	public String getINVOKE_DATE() {
		return INVOKE_DATE;
	}

	public DubboInvoke setINVOKE_DATE(String INVOKE_DATE) {
		this.INVOKE_DATE = INVOKE_DATE;
		return this;
	}

	public String getINVOKE_TIME() {
		return INVOKE_TIME;
	}

	public DubboInvoke setINVOKE_TIME(String INVOKE_TIME) {
		this.INVOKE_TIME = INVOKE_TIME;
		return this;
	}

	public String getSERVICE() {
		return SERVICE;
	}

	public DubboInvoke setSERVICE(String SERVICE) {
		this.SERVICE = SERVICE;
		return this;
	}

	public String getMETHOD() {
		return METHOD;
	}

	public DubboInvoke setMETHOD(String METHOD) {
		this.METHOD = METHOD;
		return this;
	}

	public String getCONSUMER() {
		return CONSUMER;
	}

	public DubboInvoke setCONSUMER(String CONSUMER) {
		this.CONSUMER = CONSUMER;
		return this;
	}

	public String getPROVIDER() {
		return PROVIDER;
	}

	public DubboInvoke setPROVIDER(String PROVIDER) {
		this.PROVIDER = PROVIDER;
		return this;
	}

	public String getTYPE() {
		return TYPE;
	}

	public DubboInvoke setTYPE(String TYPE) {
		this.TYPE = TYPE;
		return this;
	}

	public String getSUCCESS() {
		return SUCCESS;
	}

	public DubboInvoke setSUCCESS(String SUCCESS) {
		this.SUCCESS = SUCCESS;
		return this;
	}

	public String getFAILURE() {
		return FAILURE;
	}

	public DubboInvoke setFAILURE(String FAILURE) {
		this.FAILURE = FAILURE;
		return this;
	}

	public String getELAPSED() {
		return ELAPSED;
	}

	public DubboInvoke setELAPSED(String ELAPSED) {
		this.ELAPSED = ELAPSED;
		return this;
	}

	public String getMAX_ELAPSED() {
		return MAX_ELAPSED;
	}

	public DubboInvoke setMAX_ELAPSED(String MAX_ELAPSED) {
		this.MAX_ELAPSED = MAX_ELAPSED;
		return this;
	}

	public String getCONCURRENT() {
		return CONCURRENT;
	}

	public DubboInvoke setCONCURRENT(String CONCURRENT) {
		this.CONCURRENT = CONCURRENT;
		return this;
	}

	public String getMAX_CONCURRENT() {
		return MAX_CONCURRENT;
	}

	public DubboInvoke setMAX_CONCURRENT(String MAX_CONCURRENT) {
		this.MAX_CONCURRENT = MAX_CONCURRENT;
		return this;
	}
}



//SET NAMES utf8;
//SET FOREIGN_KEY_CHECKS = 0;
//
//DROP TABLE IF EXISTS `dubbo_invoke`;
//CREATE TABLE `dubbo_invoke` (
//	`id` varchar(255) NOT NULL DEFAULT '',
//	`invoke_date` date NOT NULL,
//	`service` varchar(255) DEFAULT NULL,
//	`method` varchar(255) DEFAULT NULL,
//	`consumer` varchar(255) DEFAULT NULL,
//	`provider` varchar(255) DEFAULT NULL,
//	`type` varchar(255) DEFAULT '',
//	`invoke_time` bigint(20) DEFAULT NULL,
//	`success` int(11) DEFAULT NULL,
//	`failure` int(11) DEFAULT NULL,
//	`elapsed` int(11) DEFAULT NULL,
//	`concurrent` int(11) DEFAULT NULL,
//	`max_elapsed` int(11) DEFAULT NULL,
//	`max_concurrent` int(11) DEFAULT NULL,
//
//	PRIMARY KEY (`id`),
//	KEY `index_service` (`service`) USING BTREE,
//KEY `index_method` (`method`) USING BTREE
//) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;
//
//SET FOREIGN_KEY_CHECKS = 1;
