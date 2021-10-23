package com.walker.mode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 统计模型	某个时间段的统计结果
 *
 * 某台服务器
 * 某个时间点
 * 受理某接口
 * 成功多少次
 * 成功共耗时
 *
 * 失败多少次
 * 失败总耗时
 *
 * logModel转logTime统计结果

 insert into W_LOG_TIME (ID, AVE_COST_NO, COUNT_OK, CATE, IP_PORT, URL, S_MTIME)
 select concat(w.minu,w.CATE) ID
 , avg(w.COST) AVE_COST_NO
 , count(*) COUNT_OK
 , w.CATE
 , w.IP_PORT_TO IP_PORT
 , w.URL URL
 , w.minu
 from
 	( select t.*, SUBSTRING_INDEX(t.S_MTIME, ':', 2) minu  from W_LOG_MODEL t ) w
 where w.minu is not null
 group by w.minu, w.CATE, w.IP_PORT_TO,w.URL


 CREATE TABLE `W_LOG_TIME` (
 `ID` varchar(32) NOT NULL DEFAULT '' COMMENT '主键',
 `AVE_COST_NO` varchar(128) DEFAULT '' COMMENT '失败平均耗时',
 `AVE_COST_OK` varchar(998) DEFAULT '' COMMENT '成功平均耗时',
 `CATE` varchar(256) DEFAULT '1970-01-01 00:00:00' COMMENT '类别',
 `COUNT_NO` varchar(128) DEFAULT '' COMMENT '失败次数',
 `COUNT_OK` varchar(128) DEFAULT '' COMMENT '成功次数',
 `IP_PORT` varchar(128) DEFAULT '' COMMENT '统计服务器ip:port',
 `S_MTIME` varchar(32) DEFAULT '' COMMENT '修改时间',
 `URL` varchar(998) DEFAULT '' COMMENT '受理接口',
 PRIMARY KEY (`ID`)
 ) ENGINE=MyISAM DEFAULT CHARSET=utf8



 */

@Entity
@Table(name = "W_LOG_TIME")
public class LogTime implements Cloneable, Serializable {
	@Id     //主键
	@Column(name="ID", columnDefinition = "varchar(32) default '' comment '主键' ")
	private String ID;
	@Column(name = "CATE", columnDefinition = "varchar(256) default '1970-01-01 00:00:00' comment '类别' ")	//sql http controller job
	private String CATE;
	@Column(name = "S_MTIME", columnDefinition = "varchar(32) default '' comment '修改时间' ")    //255
	private String S_MTIME;
	@Column(name = "IP_PORT", columnDefinition = "varchar(128) default '' comment '统计服务器ip:port' ")
	private String IP_PORT;
	@Column(name = "URL", columnDefinition = "varchar(998) default '' comment '受理接口' ")    // xxx.xxx.do
	private String URL;
	@Column(name = "COUNT_OK", columnDefinition = "varchar(128) default '' comment '成功次数' ")    //
	private String COUNT_OK;
	@Column(name = "AVE_COST_OK", columnDefinition = "varchar(998) default '' comment '成功平均耗时' ")	//
	private String AVE_COST_OK;

	@Column(name = "COUNT_NO", columnDefinition = "varchar(128) default '' comment '失败次数' ")    //
	private String COUNT_NO;
	@Column(name = "AVE_COST_NO", columnDefinition = "varchar(128) default '' comment '失败平均耗时' ")	//
	private String AVE_COST_NO;

	@Override
	public String toString() {
		return "LogTime{" +
				"ID='" + ID + '\'' +
				", CATE='" + CATE + '\'' +
				", S_MTIME='" + S_MTIME + '\'' +
				", IP_PORT='" + IP_PORT + '\'' +
				", URL='" + URL + '\'' +
				", COUNT_OK='" + COUNT_OK + '\'' +
				", AVE_COST_OK='" + AVE_COST_OK + '\'' +
				", COUNT_NO='" + COUNT_NO + '\'' +
				", AVE_COST_NO='" + AVE_COST_NO + '\'' +
				'}';
	}

	public String getID() {
		return ID;
	}

	public LogTime setID(String ID) {
		this.ID = ID;
		return this;
	}

	public String getCATE() {
		return CATE;
	}

	public LogTime setCATE(String CATE) {
		this.CATE = CATE;
		return this;
	}

	public String getS_MTIME() {
		return S_MTIME;
	}

	public LogTime setS_MTIME(String s_MTIME) {
		S_MTIME = s_MTIME;
		return this;
	}

	public String getIP_PORT() {
		return IP_PORT;
	}

	public LogTime setIP_PORT(String IP_PORT) {
		this.IP_PORT = IP_PORT;
		return this;
	}

	public String getURL() {
		return URL;
	}

	public LogTime setURL(String URL) {
		this.URL = URL;
		return this;
	}

	public LogTime setCOUNT_OK(Integer COUNT_OK) {
		this.COUNT_OK = "" + COUNT_OK;
		return this;
	}

	public Integer getCOUNT_OK() {
		if(COUNT_OK  == null || COUNT_OK.length() == 0){
			return 0;
		}
		return Integer.valueOf(COUNT_OK);
	}

	public Float getAVE_COST_OK() {
		if(this.AVE_COST_OK == null || this.AVE_COST_OK.length() == 0){
			return 0f;
		}
		return Float.valueOf(AVE_COST_OK);
	}

	public LogTime setAVE_COST_OK(Float AVE_COST_OK) {
		this.AVE_COST_OK = "" + AVE_COST_OK.intValue();
		return this;
	}

	public Integer getCOUNT_NO() {
		if(COUNT_NO  == null || COUNT_NO.length() == 0){
			return 0;
		}
		return Integer.valueOf(COUNT_NO);
	}

	public LogTime setCOUNT_NO(Integer COUNT_NO) {
		this.COUNT_NO = "" + COUNT_NO;
		return this;
	}

	public Float getAVE_COST_NO() {
		if(this.AVE_COST_NO == null || this.AVE_COST_NO.length() == 0){
			return 0f;
		}
		return Float.valueOf(AVE_COST_NO);
	}

	public LogTime setAVE_COST_NO(Float AVE_COST_NO) {
		this.AVE_COST_NO = "" + AVE_COST_NO.intValue();
		return this;
	}
}