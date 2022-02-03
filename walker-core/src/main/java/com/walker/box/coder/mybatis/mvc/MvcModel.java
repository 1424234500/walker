package com.walker.box.coder.mybatis.mvc;


import com.walker.box.coder.CodeFileFtl;
import com.walker.box.coder.CodeUtil;
import com.walker.mode.Bean;
import com.walker.util.TimeUtil;
import com.walker.util.Tools;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class MvcModel implements Serializable {
	/**
	 * com.walker.mvc.model
	 */
	CodeFileFtl packageModel;
	/**
	 * com.walker.mvc.mapper
	 */
	CodeFileFtl packageMapper;
	/**
	 * "mapper/"
	 */
	CodeFileFtl packageMapperXml;
	/**
	 * com.walker.mvc.service
	 */
	CodeFileFtl packageService;
	/**
	 * com.walker.mvc.service.impl
	 */
	CodeFileFtl packageServiceImpl;
	/**
	 * com.walker.mvc.controller
	 */
	CodeFileFtl packageController;


	CodeFileFtl packageInterface;
	CodeFileFtl packagePostman;
	CodeFileFtl packageSql;

	/**
	 * 实体类名 HelloWorld
	 */
	String modelName;
	/**
	 * 表名字 W_HELLO_WORLD
	 */
	String tableName;

	/**
	 * ID
	 */
	Column primaryKey;

	/**
	 * sqlColumns 所有字段
	 */
	List<Column> columnAll = new ArrayList<>();
	/**
	 * 表字段
	 */
	List<Column> columnList = new ArrayList<>();


//约束字段
	/**
	 * 中文表名 你好表
	 */
	String tableNameChinese;
	/**
	 * 详细说明 这是一个xxx
	 */
	String tableInfo;
	/**
	 * 英文表名前缀 默认第一个 _ 前 w_
	 */
	String tablePre;
	/**
	 * 主键是否自增 主键自增 默认0=不处理 1=自增 xxx=序列
	 */
	String keyAuto;
	/**
	 * 查询类型 默认0=表 1=视图只查询 2=java sql查询模式
	 */
	String isView;
	public static Bean isViewMap = new Bean().put("0", "表 增删查改&导出").put("1", "视图 仅查询&导出").put("2", "视图 仅查询&导出 java sql 模式");
	public String isViewInfo;


	/**
	 * 支持导出 0=是 1=否
	 */
	String isExport;
	/**
	 * 支持swagger 0=是 1=否
	 */
	String isSwagger;
	/**
	 * 支持mapper注解 0=是 1=否 须手动配置mapper导入
	 */
	String isMapperAnno;

//	生成辅助字段 初始化字段 ftl定义组合名字 放置于java中复用文件名存储
	List<CodeFileFtl> classNameToFtl = new ArrayList<>();

	String infoClass="";

	String sqlDdlDml = "";

	public void init(){
		this.modelName = (CodeUtil.turnUpperFirst(CodeUtil.turnCode(this.getTableName().substring(this.getTablePre().length()))));


		this.packageModel.setName(this.modelName);
		this.packageMapper.setName(this.modelName + "Mapper");
		this.packageMapperXml.setName(this.modelName + "Mapper");

		this.packageService.setName(this.modelName + "Service");
		this.packageServiceImpl.setName(this.modelName + "ServiceImpl");
		this.packageController.setName(this.modelName + "Controller");


		this.packageInterface.setName(this.modelName + "");
		this.packagePostman.setName(this.modelName + ".postman");
		this.packageSql.setName(this.modelName + "");

		classNameToFtl.clear();
		classNameToFtl.add(this.getPackageModel());
		//java sql查询模式
		if( ! getIsView().equals("2")) {
			classNameToFtl.add(this.getPackageMapper());
			classNameToFtl.add(this.getPackageMapperXml());
		}
		classNameToFtl.add(this.getPackageService());
		classNameToFtl.add(this.getPackageServiceImpl());
		classNameToFtl.add(this.getPackageController());
		classNameToFtl.add(this.getPackageInterface());
		classNameToFtl.add(this.getPackagePostman());
//		classNameToFtl.add(this.getPackageSql());	 //暂未实现sql ftl
		this.getPackageSql().init();

		for(CodeFileFtl item : classNameToFtl){
			item.init();
		}

		this.isViewInfo = isViewMap.get(isView, "");
		infoClass = "" +
				"/**\n" +
				" * 表全名  :\t" + tableName  + "\n" +
				" * 中文名:\t" + tableNameChinese + "\n" +
				" * 模型名:\t" + modelName + "\n" +
				" * 详细介绍:\t" + tableInfo + "\n" +
				" * 模型类型:\t" + isViewInfo + "\n" +
				" * \n" +
				" * by maker at " + TimeUtil.getTimeYmdHms() + "\n" +
				" * \n" +
				" **/" +
				"";
	}

	public void createTable() {
		StringBuilder sb = new StringBuilder();

		sb.append("\n-- 建表语句 " + this.tableNameChinese + " : " + this.tableInfo + " \n");
		sb.append("DROP TABLE if EXISTS `" + this.tableName + "`; \n\n");
		sb.append("CREATE TABLE `" + this.tableName + "` ( ");
		String key = primaryKey.tableColumnName;
		StringBuilder cs = new StringBuilder();
		StringBuilder insertK = new StringBuilder();
		StringBuilder insertV = new StringBuilder();
		for(int i = 0; i < this.columnList.size(); i++) {
			Column column = this.columnList.get(i);
			if (column.isTableColumnInfo.length() == 0) {
				cs.append("\n").append("	" + column.tableColumnName + ", ");
				sb.append("\n").append("	`" + column.tableColumnName + "` " + column.tableColumnType);
				if(column.queryEg.length() > 0){
					insertK.append("\n").append("	" + column.tableColumnName + ", ");
					insertV.append("\n").append("	" + column.queryEg + ", ");
				}
				if (column.notNull.equals("1")) {
					sb.append(" NOT NULL ");
				} else if (column.notNull.equals("2")) {
					sb.append(" AUTO_INCREMENT ");
					key = column.tableColumnName;   //自增优先单主键
				} else if (column.notNull.equals("3")) {

				}
				if (column.defValue.length() > 0) {
					sb.append(" DEFAULT " + column.defValue);
				}
				if (column.nameChinese.length() > 0) {
					sb.append(" COMMENT '" + column.nameChinese + "'");
				}
				sb.append(", ");

			}
		}

		sb.append("\n\n	PRIMARY KEY (`" + key + "`), ");
		if (!key.equals(primaryKey.tableColumnName)) { //额外逻辑主键唯一索引
			sb.append("\n	UNIQUE KEY `IDX_" + primaryKey.tableColumnName + "` (`" + primaryKey.tableColumnName + "`), ");
		}
		sb.setLength(sb.length() - ", ".length());
		cs.setLength(cs.length() - ", ".length());
		if(insertK.length() > 0) {
			insertK.setLength(insertK.length() - ", ".length());
			insertV.setLength(insertV.length() - ", ".length());
		}
		sb.append("\n) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='" + this.tableNameChinese + "'").append("\n");
		sb.append("; ").append("\n\n");

		sb.append("\n-- 清空表参考sql " + this.tableNameChinese + " \n");
		sb.append("TRUNCATE TABLE `" + this.tableName + "`; \n");

		sb.append("\n-- 样例数据初始化sql " + this.tableNameChinese + " \n");
		if(insertK.length() > 0) {
			String insertSql = "insert into " + this.tableName + "(" + insertK + "\n) values (" + insertV + "\n); ";
			insertSql = insertSql.replace("\n", " ").replaceAll("  +", " ");
			sb.append(insertSql + "\n");
		}

		sb.append("\n-- 查询表参考sql " + this.tableNameChinese + " \n");
		sb.append("select " + cs + " \nfrom " + this.tableName + "; \n");

		Tools.out("\n" + sb);
		this.sqlDdlDml = sb.toString();
	}
}




