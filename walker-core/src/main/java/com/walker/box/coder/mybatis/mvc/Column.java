package com.walker.box.coder.mybatis.mvc;

import com.walker.box.coder.CodeUtil;
import com.walker.core.mode.Bean;
import com.walker.core.util.Tools;
import lombok.Data;

@Data
public class Column{

	/**
	 * java bean 变量名 TEST_ID -> testId
	 */
	String instanseName;
	/**
	 * java 类型 VARCHAR -> String	TIMESTAMP -> String
	 */
	String instanseType;

	/**
	 * table 字段 TEST_ID
	 */
	String tableColumnName;
	/**
	 * table 字段类型 VARCHAR
	 */
	String tableColumnType;


	/**
	 * 字段中文名
	 */
	String nameChinese;
	/**
	 * 字段 默认0=可null 1=not null 2=自增 3=逻辑主键
	 */
	String notNull;
	public static Bean notNullMap = new Bean().put("0", "").put("1", "不可为空").put("2", "自增主键").put("3", "逻辑主键");
	public String notNullInfo;
	public static Bean notNullMustMap = new Bean().put("0", "选填").put("1", "必填").put("2", "更新时必填 插入时选填").put("3", "必填");
	public String notNullMustInfo;

	/**
	 * 默认值str '' 兼容mysql函数 所以字符串自带'
	 */
	String defValue;
	/**
	 * 字段介绍详细
	 */
	String info;
	/**
	 * 控制前端参数 查询必要性 默认0=选填 1=必填 2=不可查询 仅仅提示前端 接口文档
	 * 2时 不做xml匹配规则
	 */
	String mustQuery;
	public static Bean mustQueryMap = new Bean().put("0", "选填").put("1", "必填").put("2", "不可查询");
	String mustQueryInfo;
	/**
	 * 控制前端参数样例值   兼容mysql函数 所以字符串自带' 前端参数接口文档需注意
	 */
	String queryEg;
	/**
	 * 样例值2 str ''查询区间时终点数据
	 */
	String queryEg2;
	/**
	 * xml模糊匹配 [默认0=like %x% 1='='] 非字符串不允许like
	 */
	String xmlLike;
	public static Bean xmlLikeMap = new Bean().put("0", "模糊匹配 eg: XX like '%xxx%' ").put("1", "全匹配 eg: XX = 'xxx' ").put("2", "");
	String xmlLikeInfo;
	/**
	 * 附加字段构造 Begin/End区间 默认0=不处理
	 */
	String xmlDeta;
	public static Bean xmlDetaMap = new Bean().put("1", "区间 Begin/End 查询 eg: XX >= 'XxBegin' and XX < 'XxEnd' ").put("0", "");
	String xmlDetaInfo;
	/**
	 * 附加in字段构造s 默认0=不处理
	 */
	String xmlIn;
	public static Bean xmlInMap = new Bean().put("1", "多选 in 查询 eg: XX in ('Xx1','Xx2','Xx3') ").put("0", "");
	String xmlInInfo;

	/**
	 * 是否是表字段 默认0是 1否
	 */
	String isTableColumn;
	public static Bean isTableColumnMap = new Bean().put("1", "翻译字段 仅仅查询结果返回 做展示用").put("0", "");
	String isTableColumnInfo;


	String infoProperties="";
	/**
	 * 类型转换
	 * 默认 VARCHAR = String
	 * TIMESTAMP -> String -> TIMESTAMP '2020-10-19 00:00:00'
	 */
	String javaToTableColumnInsert;
	/**
	 * 类型转换
	 * 默认 VARCHAR(20) = String
	 * TIMESTAMP -> String -> to_char(CAP_TIME, 'YYYY-MM-DD HH24:MI:SS')
	 */
	String tableColumnToJavaSelect;
	/**
	 * xml类型转换 VARCHAR(20) -> VARCHAR
	 */
	String tableColumnTypeXml;

	/**
	 * 样例值去符号
	 */
	String queryEgNoQuoto;
	/**
	 * 样例值去符号
	 */
	String queryEgNoQuoto2;

	//	org.apache.ibatis.type.JdbcType
	public static Bean tableColumnTypeXmlMap = new Bean()
		.put("TEXT", "VARCHAR")
		.put("LONG", "BIGINT")
		;


	public void init(){
//		字段格式化
		this.notNull = this.notNull.toUpperCase().replace('Y', '1').replace("TRUE", "1");

//		非表字段默认不可查询
		if(this.isTableColumn.equals("1") && this.xmlLike.equals("0")){
			this.xmlLike = "2";
		}


//		默认值处理
		if(this.defValue.length() > 0 && this.defValue.endsWith("'") && !this.defValue.startsWith("'")){
			this.defValue = "'" + this.defValue;
		}
//		样例值处理
		if(this.queryEg.length() > 0 && this.queryEg.endsWith("'") && !this.queryEg.startsWith("'")){
			this.queryEg = "'" + this.queryEg;
		}
		if(this.queryEg2.length() > 0 && this.queryEg2.endsWith("'") && !this.queryEg2.startsWith("'")){
			this.queryEg2 = "'" + this.queryEg2;
		}
		this.queryEgNoQuoto = CodeUtil.noQuoto(this.queryEg);
		this.queryEgNoQuoto2 = CodeUtil.noQuoto(this.queryEg2);



		javaToTableColumnInsert = this.tableColumnName;
		tableColumnToJavaSelect = this.tableColumnName;
		String javaType = CodeUtil.turnSqlType2JavaType(this.getTableColumnType());
		//		非字符串不允许like
		if(this.xmlLike.equals("0") && !javaType.equals("String")){
			this.xmlLike = "2";
			Tools.out("非字符串不允许like turn to no " + this);
		}

		if(javaType.equals("Date")){//编码 解码 to_char(S_TIME, 'yyyy-xxx') as S_TIME
		}
		this.setInstanseType(javaType);
		this.setInstanseName(CodeUtil.turnCode(this.getTableColumnName()));


		if(this.getDefValue() != null && this.getDefValue().endsWith("'") && !this.getDefValue().startsWith("'")){  //excel首字符'异常
			this.setDefValue("'" + this.getDefValue());
		}

		this.notNullInfo = notNullMap.get(notNull, "");
		this.notNullMustInfo = notNullMustMap.get(notNull, "");

		this.mustQueryInfo = mustQueryMap.get(mustQuery, "");
		this.xmlLikeInfo = xmlLikeMap.get(xmlLike, "");
		this.xmlDetaInfo = xmlDetaMap.get(xmlDeta, "");
		this.xmlInInfo = xmlInMap.get(xmlIn, "");
		this.isTableColumnInfo = isTableColumnMap.get(isTableColumn, "");

		infoProperties = "" +
			"	/**\n" +
			(getNotNullInfo().length() > 0 ?
			"	 * " + getNotNullInfo() + "\n\n" : "" ) +
			"	 * 字段  :\t" + tableColumnName  + "\n" +
			"	 * 字段名:\t" + nameChinese + "\n" +
			"	 * 详细介绍:\t" + info + "\n" +
			"	 * 字段类型:\t" + tableColumnType + "\n" +
			"	 * Java类型:\t" + instanseType + "\n" +
			"	 * 前端查询:\t" + getMustQueryInfo() + "\n" +
			(getXmlLikeInfo().length() > 0 ?
			"	 * 支持查询: " + getXmlLikeInfo() + ",\n" : "" ) +
			(xmlDetaInfo.length() > 0 ?
			"	 * 支持查询: " + xmlDetaInfo + "\n" : "" ) +
			(xmlInInfo.length() > 0 ?
			"	 * 支持查询: " + xmlInInfo + "\n" : "" ) +
			"	 * \n" +
			"	**/" +
			"";

		this.tableColumnTypeXml = this.tableColumnType;
		if(this.tableColumnTypeXml.indexOf("(") > 0){
			this.tableColumnTypeXml = this.tableColumnTypeXml.substring(0, this.tableColumnTypeXml.indexOf("("));
		}
		this.tableColumnTypeXml = this.tableColumnTypeXmlMap.get(this.tableColumnTypeXml, this.tableColumnTypeXml);


	}

}
