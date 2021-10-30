package com.walker.mode;

import com.walker.service.Config;
import com.walker.system.Pc;
import com.walker.util.LangUtil;
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
@Table(name = "W_LOG_MODEL"
		, indexes = {
//                @Index(name = "INDEX_W_AREA_PATH", columnList = "PATH"),      //索引不能超过1000
//                @Index(name = "INDEX_W_AREA_PATH_NAME", columnList = "PATH_NAME"),
		@Index(name = "INDEX_W_LOG_MODEL_URL", columnList = "URL")
		, @Index(name = "INDEX_W_LOG_MODEL_WAY", columnList = "WAY")
		, @Index(name = "INDEX_W_LOG_MODEL_CATE", columnList = "CATE")
}
)

public class LogModel implements Cloneable, Serializable {
	@Id     //主键
	@Column(name="ID", columnDefinition = "varchar(32) default '' comment '主键' ")
	private String ID;
	@Column(name = "USER", columnDefinition = "varchar(256) default '1970-01-01 00:00:00' comment '用户' ")
    private String USER;
	@Column(name = "CATE", columnDefinition = "varchar(64) default '1970-01-01 00:00:00' comment '类别' ")
	private String CATE;
	@Column(name = "S_MTIME", columnDefinition = "varchar(32) default '' comment '修改时间' ")    //255
	private String S_MTIME = TimeUtil.getTimeYmdHmss();
	@Column(name = "IP_PORT_FROM", columnDefinition = "varchar(64) default '' comment '用户的ip:port' ")
	private String IP_PORT_FROM;
	@Column(name = "IP_PORT_TO", columnDefinition = "varchar(64) default '' comment '受理服务器的ip:port' ")
	private String IP_PORT_TO;
	@Column(name = "URL", columnDefinition = "varchar(512) default '' comment 'URL' ")    //www.baidu.com	类名
	private String URL;
	@Column(name = "WAY", columnDefinition = "varchar(64) default '' comment '方式' ")    //post	任务id
	private String WAY;
	@Column(name = "ARGS", columnDefinition = "varchar(998) default '' comment '参数' ")	//wd=123	触发器规则
	private String ARGS;
	@Column(name = "IS_EXCEPTION", columnDefinition = "varchar(2) default '' comment '1是0否异常' ")
	private String IS_EXCEPTION;
	@Column(name = "EXCEPTION", columnDefinition = "varchar(998) default '' comment '异常详情' ")
	private String EXCEPTION;
	@Column(name = "IS_OK", columnDefinition = "varchar(2) default '' comment '1是0否成功' ")
	private String IS_OK;
	@Column(name = "RES", columnDefinition = "varchar(1998) default '' comment '访问结果' ")
	private String RES;
	@Column(name = "COST", columnDefinition = "varchar(128) default '' comment '耗时' ")
	private String COST;

	@Column(name = "ABOUT", columnDefinition = "varchar(998) default '' comment '说明' ")
	private String ABOUT;


	public static LogModel getDefaultModel(){
		LogModel logModel = new LogModel()
				.setS_MTIME(TimeUtil.getTimeYmdHmss())
				.setIS_EXCEPTION(Config.FALSE)
				.setIS_OK(Config.TRUE)
				.setIP_PORT_FROM(Pc.getIps().toString())
				.setIP_PORT_TO(Pc.getIps().toString())
				;
		return logModel;

	}
	public void make(){
		if(getID() == null || getID().length() == 0){
			setID(LangUtil.getTimeSeqId());
		}
		if(getS_MTIME() == null || getS_MTIME().length() == 0) {
			setS_MTIME(TimeUtil.getTimeYmdHmss());
		}
		if(getCOST() == 0){
			long start = TimeUtil.format(getS_MTIME(), TimeUtil.ymdhmsS);
			setCOST(System.currentTimeMillis() - start);
		}
		if(getEXCEPTION() != null && getEXCEPTION().length() != 0){
			setIS_EXCEPTION(Config.TRUE);
		}
		if(getIP_PORT_FROM() == null || getIP_PORT_FROM().length() == 0){
			setIP_PORT_FROM(Pc.getIps().toString());
		}
		if(getIP_PORT_TO() == null || getIP_PORT_TO().length() == 0){
			setIP_PORT_TO(Pc.getIps().toString());
		}
	}



	public String getID() {
		return ID;
	}

	public String getCATE() {
		return CATE;
	}

	public LogModel setCATE(String CATE) {
		this.CATE = CATE;
		return this;
	}

	public LogModel setID(String ID) {
		this.ID = ID;
		return this;
	}

	public String getWAY() {
		return WAY;
	}

	public LogModel setWAY(String WAY) {
		WAY = Config.cutString(998, WAY);

		this.WAY = WAY;
		return this;
	}

	public String getUSER() {
		return USER;
	}

	public LogModel setUSER(String USER) {
		this.USER = USER;
		return this;
	}

	public String getS_MTIME() {
		return S_MTIME;
	}

	public LogModel setS_MTIME(String s_MTIME) {
		S_MTIME = s_MTIME;
		return this;
	}

	public String getIP_PORT_FROM() {
		return IP_PORT_FROM;
	}

	public LogModel setIP_PORT_FROM(String IP_PORT_FROM) {
		this.IP_PORT_FROM = IP_PORT_FROM;
		return this;
	}

	public String getIP_PORT_TO() {
		return IP_PORT_TO;
	}

	public LogModel setIP_PORT_TO(String IP_PORT_TO) {
		this.IP_PORT_TO = IP_PORT_TO;
		return this;
	}

	public String getURL() {
		return URL;
	}

	public LogModel setURL(String URL) {
		URL = Config.cutString(998, URL);

		this.URL = URL;
		return this;
	}

	@Override
	public String toString() {
		return "LogModel{" +
				"ID='" + ID + '\'' +
				", USER='" + USER + '\'' +
				", CATE='" + CATE + '\'' +
				", S_MTIME='" + S_MTIME + '\'' +
				", IP_PORT_FROM='" + IP_PORT_FROM + '\'' +
				", IP_PORT_TO='" + IP_PORT_TO + '\'' +
				", URL='" + URL + '\'' +
				", WAY='" + WAY + '\'' +
				", ARGS='" + ARGS + '\'' +
				", IS_EXCEPTION='" + IS_EXCEPTION + '\'' +
				", EXCEPTION='" + EXCEPTION + '\'' +
				", IS_OK='" + IS_OK + '\'' +
				", RES='" + RES + '\'' +
				", COST='" + COST + '\'' +
				", ABOUT='" + ABOUT + '\'' +
				'}';
	}

	public String getARGS() {
		return ARGS;
	}

	public LogModel setARGS(String ARGS) {
		ARGS = Config.cutString(998, ARGS);

		this.ARGS = ARGS;
		return this;
	}

	public String getIS_EXCEPTION() {
		return IS_EXCEPTION;
	}

	public LogModel setIS_EXCEPTION(String IS_EXCEPTION) {
		this.IS_EXCEPTION = IS_EXCEPTION;
		return this;
	}

	public String getEXCEPTION() {
		return EXCEPTION;
	}

	public LogModel setEXCEPTION(String EXCEPTION) {
		EXCEPTION = Config.cutString(998, EXCEPTION);
		this.EXCEPTION = EXCEPTION;
		return this;
	}

	public String getIS_OK() {
		return IS_OK;
	}

	public LogModel setIS_OK(String IS_OK) {
		this.IS_OK = IS_OK;
		return this;
	}

	public String getRES() {
		return RES;
	}

	public LogModel setRES(String RES) {
		this.RES = RES;
		RES = Config.cutString(Config.MAX_VARCHAR_LEN, RES);
		return this;
	}

	public Long getCOST() {
		if(this.COST == null || this.COST.length() == 0){
			return 0l;
		}
		return Long.valueOf(COST);
	}

	public LogModel setCOST(Long COST) {
		this.COST = "" + COST;
		return this;
	}

	public String getABOUT() {
		return ABOUT;
	}

	public LogModel setABOUT(String ABOUT) {
		ABOUT = Config.cutString(998, ABOUT);

		this.ABOUT = ABOUT;
		return this;
	}
}