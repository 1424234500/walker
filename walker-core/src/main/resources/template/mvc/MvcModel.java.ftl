<#include "MvcBase.java.ftl" />
package ${packageModel.packagePath};

import java.io.Serializable;
import com.dahua.saas.base.BeanInterpreter;
import ${localPackageJson}
<#if isSwagger?starts_with("0")>import io.swagger.annotations.*;</#if>

${infoClass}
<#if isSwagger?starts_with("0")>@ApiModel(value = "${tableNameChinese}", description = "${tableInfo}") </#if>
public class ${packageModel.className} implements Serializable, BeanInterpreter {

<#list columnAll as item>
<#--
    /**
     * 表字段  :	ID
     * 表字段名:	主键自增
     * 详细介绍:	主键自增
     * 前端查询:	选填
     * 查询匹配:	全值匹配=,
     **/
-->
${item.infoProperties}
<#--
    private Long id;
-->
<#--@ApiModelProperty(value = "主键自增", required = false, hidden = false, example = "a")-->
<#if isSwagger?starts_with("0")>    @ApiModelProperty(value = "${item.nameChinese}"<#if item.mustQuery?starts_with("0")>, required = false<#elseif item.mustQuery?starts_with("1")>, required = true<#else>, hidden = true</#if><#if (item.queryEgNoQuoto?length > 0) >, example = "${item.queryEgNoQuoto}"</#if>) </#if>
    private ${item.instanseType} ${item.instanseName};
<#if item.xmlDeta?starts_with("1")>
    /**
     * 区间查询 ${item.nameChinese} ${item.tableColumnName} >= 起点值
     */
<#if isSwagger?starts_with("0")>    @ApiModelProperty(value = "区间查询 ${item.nameChinese} ${item.tableColumnName} >= 起点值  ", hidden = true, required = false<#if (item.queryEgNoQuoto?length > 0) >, example = "${item.queryEgNoQuoto}"</#if>)</#if>
    private ${item.instanseType} ${item.instanseName}Begin;
    /**
     * 区间查询 ${item.nameChinese} ${item.tableColumnName} < 终点值
     */
<#if isSwagger?starts_with("0")>    @ApiModelProperty(value = "区间查询 ${item.nameChinese} ${item.tableColumnName} < 终点值  ", hidden = true, required = false<#if (item.queryEgNoQuoto?length > 0) >, example = "${item.queryEgNoQuoto}"</#if>)</#if>
    private ${item.instanseType} ${item.instanseName}End;
</#if>
<#if item.xmlIn?starts_with("1")>
    /**
     * ${item.nameChinese} 多选匹配 in []
     */
<#if isSwagger?starts_with("0")>    @ApiModelProperty(value = "${item.nameChinese} 多选匹配", hidden = true, required = false<#if (item.queryEgNoQuoto?length > 0) >, example = "[\"${item.queryEgNoQuoto}\", \"${item.queryEgNoQuoto}1\", \"${item.queryEgNoQuoto}2\"]"</#if>)</#if>
    private ${item.instanseType}[] ${item.instanseName}s;
</#if>



</#list>

<#list columnAll as item>
<#--
    public Long getId(){
        return id;
    }
    public ParkChannel setId(Long id){
        this.id = id;
        return this;
    }
-->
    public ${item.instanseType} get${item.instanseName?cap_first}(){
        return ${item.instanseName};
    }
    public ${packageModel.className} set${item.instanseName?cap_first}(${item.instanseType} ${item.instanseName}){
        this.${item.instanseName} = ${item.instanseName};
        return this;
    }
<#if item.xmlDeta?starts_with("1")>
    public ${item.instanseType} get${item.instanseName?cap_first}Begin(){
        return ${item.instanseName}Begin;
    }
    public ${packageModel.className} set${item.instanseName?cap_first}Begin(${item.instanseType} ${item.instanseName}Begin){
        this.${item.instanseName}Begin = ${item.instanseName}Begin;
        return this;
    }
    public ${item.instanseType} get${item.instanseName?cap_first}End(){
        return ${item.instanseName}End;
    }
    public ${packageModel.className} set${item.instanseName?cap_first}End(${item.instanseType} ${item.instanseName}End){
        this.${item.instanseName}End = ${item.instanseName}End;
        return this;
    }
</#if>
<#if item.xmlIn?starts_with("1")>
    public ${item.instanseType}[] get${item.instanseName?cap_first}s(){
        return ${item.instanseName}s;
    }
    public ${packageModel.className} set${item.instanseName?cap_first}s(${item.instanseType}[] ${item.instanseName}s){
        this.${item.instanseName}s = ${item.instanseName}s;
        return this;
    }
</#if>


</#list>

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
    @Override
    public void interpret() {


    }

}


