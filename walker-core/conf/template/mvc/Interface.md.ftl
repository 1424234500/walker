<#include "MvcBase.java.ftl" />
<#assign localTitle1="## ">
<#assign localHttpProto=" HTTP/1.1">
<#assign localBody="#### body">
<#assign localBodyInfo="#### url参数&body说明">
<#assign localResponse="#### 返回结果">
<#assign localResponseInfo="#### 返回结果说明">
<#assign localLine="---------------------">

${infoClass}

<#if isView?starts_with("0") >

${localTitle1}批量添加  ${tableNameChinese} ${modelName} ${tableName}
POST ${localHttpProto}
${localIpPortPre}/${packageModel.instanseName}

${localBody}
[{
<#list columnList as item>
    <#if (item_index > 0) >, </#if>"${item.instanseName}" : <#if (item.queryEgNoQuoto?length > 0) >"${item.queryEgNoQuoto}"<#else>null</#if>
</#list>
}]

${localBodyInfo}
<#list columnList as item>
${item.instanseName}	:	${item.instanseType}	${item.notNullMustInfo}	${item.nameChinese}<#if (item.info?length > 0) >	${item.info}</#if><#if (item.defValue?length > 0) >	默认值 ${item.defValue}</#if>
</#list>

${localResponse}
{
  "code": "200",
  "message": "success",
  "totalCount": 0,
  "results": 1
}
${localResponseInfo}
code	:	Integer	结果状态码	200 : 正常 其他不正常 详见错误编码
message	:	String	提示信息
totalCount	:	Integer	结果总数(查询)
results :   Integer 影响数据条数

${localLine}
${localTitle1}更新  ${tableNameChinese} ${modelName} ${tableName} by ${primaryKey.instanseName}Old

PUT ${localHttpProto}
${localIpPortPre}/${packageModel.instanseName}/{${primaryKey.instanseName}Old}
案例：
${localIpPortPre}/${packageModel.instanseName}/<#if (primaryKey.queryEgNoQuoto?length > 0) >${primaryKey.queryEgNoQuoto}<#else>${primaryKey.instanseName}</#if>

${localBody}
{
<#list columnList as item>
    <#if (item_index > 0) >, </#if>"${item.instanseName}" : <#if (item.queryEgNoQuoto?length > 0) >"${item.queryEgNoQuoto}"<#else>null</#if>
</#list>
}

${localBodyInfo}
${primaryKey.instanseName}Old	:	String	必填	更新对象的旧主键

<#list columnList as item>
${item.instanseName}	:	${item.instanseType}	${item.notNullMustInfo}	${item.nameChinese}<#if (item.info?length > 0) >	${item.info}</#if><#if (item.defValue?length > 0) >	默认值 ${item.defValue}</#if>
</#list>

${localResponse}
{
  "code": "200",
  "message": "success",
  "totalCount": 0,
  "results": 1
}

${localResponseInfo}
code	:	Integer	结果状态码	200 : 正常 其他不正常 详见错误编码
message	:	String	提示信息
totalCount	:	Integer	结果总数(查询时)
results :   Integer 影响数据条数


${localLine}
${localTitle1}删除 ${tableNameChinese} ${modelName} ${tableName}  by ${primaryKey.instanseName}s

DELETE ${localHttpProto}
${localIpPortPre}/${packageModel.instanseName}/{${primaryKey.instanseName}s}
案例：
${localIpPortPre}/${packageModel.instanseName}/<#if (primaryKey.queryEgNoQuoto?length > 0) >${primaryKey.queryEgNoQuoto},${primaryKey.queryEgNoQuoto}1,${primaryKey.queryEgNoQuoto}2,...<#else>${primaryKey.instanseName},${primaryKey.instanseName}1,${primaryKey.instanseName}2,...</#if>

${localBody}
${localBodyInfo}
${primaryKey.instanseName}s	:	String	${primaryKey.notNullMustInfo}	${primaryKey.nameChinese}<#if (primaryKey.info?length > 0) >	${primaryKey.info}</#if>	操作对象的主键拼接 如: "${primaryKey.instanseName},${primaryKey.instanseName}1,${primaryKey.instanseName}2,...."

${localResponse}
{
  "code": "200",
  "message": "success",
  "totalCount": 0,
  "results": 2
}

${localResponseInfo}
code	:	Integer	结果状态码	200 : 正常 其他不正常 详见错误编码
message	:	String	提示信息
totalCount	:	Integer	结果总数(查询时)
results :   Integer 影响数据条数

</#if>

${localLine}
${localTitle1}查询  ${tableNameChinese} ${modelName} ${tableName}

POST ${localHttpProto}
${localIpPortPre}/${packageModel.instanseName}/finds?page={page}&pageSize={pageSize}&orderBy={orderBy}
案例：
${localIpPortPre}/${packageModel.instanseName}/finds?page=1&pageSize=10&orderBy=${primaryKey.tableColumnName} DESC

${localBody}
<#--默认只有字符串才 like 第一行 不可查询? ,号问题-->
{
    "_" : ""
<#list columnList as item>
<#if item.xmlLike?starts_with("0")>
    , "${item.instanseName}" : <#if (item.queryEgNoQuoto?length > 0) >"${item.queryEgNoQuoto}"<#else>null</#if>
<#elseif item.xmlLike?starts_with("1")>
    , "${item.instanseName}" : <#if (item.queryEgNoQuoto?length > 0) >"${item.queryEgNoQuoto}"<#else>null</#if>
<#else>
</#if>
<#if item.xmlDeta?starts_with("1")>
    , "${item.instanseName}Begin" : <#if (item.queryEgNoQuoto?length > 0) >"${item.queryEgNoQuoto}"<#else>null</#if>
    , "${item.instanseName}End" : <#if (item.queryEgNoQuoto2?length > 0) >"${item.queryEgNoQuoto2}"<#else>null</#if>
</#if>
<#if item.xmlIn?starts_with("1")>
    , "${item.instanseName}s" : ["${item.queryEgNoQuoto}", "${item.queryEgNoQuoto}1", "${item.queryEgNoQuoto}2", "..."]
</#if>
</#list>
}

${localBodyInfo}
page	:	Integer	选填	默认查询第1页
pageSize	:	Integer	选填	默认查询10条
orderBy	:	String	选填	排序方式

<#list columnList as item>
<#if item.xmlLike?starts_with("2")>
<#--不可查询 ,号问题-->
<#else>
${item.instanseName}	:	${item.instanseType}	${item.mustQueryInfo}   ${item.nameChinese}<#if (item.info?length > 0) >	${item.info}</#if>  ${item.xmlLikeInfo}
</#if>
<#if item.xmlDeta?starts_with("1")>
${item.instanseName}Begin	:	${item.instanseType}	选填	${item.nameChinese}<#if (item.info?length > 0) >	${item.info}</#if>	区间查询 >= 起点值
${item.instanseName}End	:	${item.instanseType}	选填	${item.nameChinese}<#if (item.info?length > 0) >	${item.info}</#if>	区间查询 < 终点值
</#if>
<#if item.xmlIn?starts_with("1")>
${item.instanseName}s	:	String[]	选填	${item.nameChinese}<#if (item.info?length > 0) >	${item.info}</#if>	多选 in [xxx,xx,x]
</#if>
</#list>

${localResponse}
{
    "code": "200",
    "message": "success",
    "totalCount": 0,
    "results": [
        {
<#list columnAll as item>
            <#if (item_index > 0) >, </#if>"${item.instanseName}" : <#if (item.queryEgNoQuoto?length > 0) >"${item.queryEgNoQuoto}"<#else>null</#if>
</#list>
        }
        , {
<#list columnAll as item>
            <#if (item_index > 0) >, </#if>"${item.instanseName}" : <#if (item.queryEgNoQuoto?length > 0) >"${item.queryEgNoQuoto}"<#else>null</#if>
</#list>
    }]
}

${localResponseInfo}
code	:	Integer	结果状态码	200 : 正常 其他不正常 详见错误编码
message	:	String	提示信息
totalCount	:	Integer	结果总数(查询)

results	:	{   结果数据对象集合
<#list columnAll as item>
    ${item.instanseName}	:	${item.instanseType}<#if (item.isTableColumnInfo?length > 0) >	${item.isTableColumnInfo}</#if>   ${item.nameChinese}<#if (item.info?length > 0) >	${item.info}</#if>
</#list>
}
