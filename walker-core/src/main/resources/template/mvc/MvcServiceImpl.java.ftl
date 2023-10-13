<#include "MvcBase.java.ftl" />
package ${packageServiceImpl.packagePath};

import ${packageModel.packagePath}.*;
import ${packageMapper.packagePath}.*;
import ${packageService.packagePath}.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import ${localPackageJson}
import ${localPackageBase}
import ${localPackageSlf4j}
import ${localPackagePager}

${infoClass}
@Service("${packageService.instanseName}")
public class ${packageServiceImpl.className} implements ${packageService.className} {

<@slf4j className="${packageServiceImpl.className}" />
<#if isView?starts_with("0") >
    @Autowired
    @Qualifier("${packageMapper.instanseName}")
    private ${packageMapper.className} ${packageMapper.instanseName};


    @Override
    public ${Long} ${adds}(List<${packageModel.className}> list)  {
        return ${packageMapper.instanseName}.adds(list);
    }
    @Override
    public ${Long} ${update}(${packageModel.className} ${object}, String ${id})  {
        return ${packageMapper.instanseName}.update(${object}, ${id});
    }
    @Override
    public ${Long} ${deletes}(List<String> list)  {
        return ${packageMapper.instanseName}.deletes(list);
    }
    @Override
    public ${Long} ${deletesByObject}(${packageModel.className} ${object})  {
        return ${packageMapper.instanseName}.deletesByObject(${object});
    }
</#if>

<#if isView?starts_with("2") >
<#-- java 拼 sql 查询-->
    @Override
    public List<${packageModel.className}> finds(${packageModel.className} object, Pager pager)  {
        List<${packageModel.className}> res = PaasServiceUtil.finds(makeSqlQuery(object, pager), pager, makeSqlCount(object), new TypeReference<${packageModel.className}>(){});
        for (${packageModel.className} re : res) {
            re.interpret();
        }
        return res;
    }
    @Override
    public Long count(${packageModel.className} object) {
        return PaasServiceUtil.count(makeSqlCount(object));
    }

    private String makeSqlCount(${packageModel.className} object) {
        return makeSql(object, 2, null);
    }
    private String makeSqlQuery(${packageModel.className} object, Pager pager) {
        return makeSql(object, 1, pager);
    }

    private String makeSql(${packageModel.className} object, int type, Pager pager)   {
        StringBuffer sbf = new StringBuffer();
        sbf.append("SELECT   ");
        if (1 == type) {
<#list columnList as item>
            sbf.append("<#if (item_index > 0) >, </#if>${item.tableColumnToJavaSelect} AS ${item.instanseName}");
</#list>
        } else if (2 == type) {
            sbf.append(" COUNT(1) AS count ");
        }
        sbf.append(" FROM ${tableName} ");
        sbf.append(" WHERE 1=1");



<#list columnList as item>
<#if item.xmlLike?starts_with("0")>
<#--默认只有字符串才 like-->
        if (object.get${item.instanseName?cap_first}() != null  && object.get${item.instanseName?cap_first}().length() > 0  ){
            sbf.append(" AND ${item.tableColumnName} like '%" + object.get${item.instanseName?cap_first}() + "%' ");
        }
<#elseif item.xmlLike?starts_with("1")>
        if (object.get${item.instanseName?cap_first}() != null  ){
            sbf.append(" AND ${item.tableColumnName} = '" + object.get${item.instanseName?cap_first}() + "' ");
        }
<#else>
<#--不可查询-->
</#if>
<#if item.xmlDeta?starts_with("1")>
<#--TIMESTAMP查询问题 是否需要显示格式化 云数据库utc问题?-->
        if (object.get${item.instanseName?cap_first}Begin() != null && object.get${item.instanseName?cap_first}Begin().length() > 0){
            try {
<#if item.tableColumnType?starts_with("TIMESTAMP")>
                sbf.append(" AND ${item.tableColumnName} >=  TIMESTAMP '" + DateUtil.formatUtc(object.get${item.instanseName?cap_first}Begin()) + "' ");
<#else>
                sbf.append(" AND ${item.tableColumnName} >= '" + object.get${item.instanseName?cap_first}Begin() + "' ");
</#if>
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        if (object.get${item.instanseName?cap_first}End() != null && object.get${item.instanseName?cap_first}End().length() > 0){
            try {
<#if item.tableColumnType?starts_with("TIMESTAMP")>
                sbf.append(" AND ${item.tableColumnName} <  TIMESTAMP '" + DateUtil.formatUtc(object.get${item.instanseName?cap_first}End()) + "' ");
    <#else>
                sbf.append(" AND ${item.tableColumnName} < '" + object.get${item.instanseName?cap_first}End() + "' ");
    </#if>
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
</#if>
<#if item.xmlIn?starts_with("1")>
        if(object.get${item.instanseName?cap_first}s() != null   && object.get${item.instanseName?cap_first}s().length > 0 && object.get${item.instanseName?cap_first}s()[0] != null && object.get${item.instanseName?cap_first}s()[0].length() > 0){
            sbf.append(" AND ${item.tableColumnName} IN ( " +  StringUtil.appendQuotes(StringUtils.join(object.get${item.instanseName?cap_first}s(), ","), ",") + " ) ");
        }
</#if>
</#list>
        if (1 == type) {
            if(pager.getOrderBy() != null && pager.getOrderBy().length() > 0) {
                sbf.append(" ORDER BY  " + pager.getOrderBy());
            }
            sbf.append(" LIMIT " + pager.getPageSize() + " OFFSET " + (pager.getStartPosition() ));
        }
        return sbf.toString();
    }
<#else>
<#--常规查询表 xml-->
    @Override
    public ${Long} ${count}(${packageModel.className} ${object})  {
        return ${packageMapper.instanseName}.count(${object});
    }
    @Override
    public List<${packageModel.className}> ${finds}(${packageModel.className} ${object}, Pager pager)  {
        List<${packageModel.className}> list = ${packageMapper.instanseName}.finds(${object}, pager);
        for (${packageModel.className} item : list) {
            item.interpret();
        }
        ${Long} size = ${packageMapper.instanseName}.count(${object});
        pager.setTotalCount(0L + size);
        return list;
    }

</#if>
}