<#include "MvcBase.java.ftl" />
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageMapper.packagePath}.${packageMapper.className}">
    <!-- 通用查询映射结果 是否要用 ? -->
    <resultMap id="BaseResultMap" type="${packageModel.packagePath}.${packageModel.className}">
<#list columnList as item>
    <#--
        <id column="NODE_CODE" property="nodeCode" jdbcType="VARCHAR"/>
    -->
        <id column="${item.tableColumnName}" property="${item.instanseName}" jdbcType="${item.tableColumnTypeXml}"/>
</#list>
    </resultMap>

    <!--AutoMapperInterceptor-->
    <!--    定义表名-->
    <sql id="Table">${tableName}</sql>
    <!--兼容oracle-->
    <sql id="Sequence"></sql>
    <!--插件需要-->
    <sql id="AutoJoin"></sql>

<!-- 定义列组合 插入用 -->
    <sql id="Base_Column_List">
<#list columnList as item>
        <#--  NAME, NODE_TYPE -->
        <#if (item_index > 0) >, </#if>${item.tableColumnName}
</#list>
    </sql>

<!-- 定义列组合 别名 编码 查询用-->
    <sql id="Base_Column_List_Select">
<#list columnList as item>
        <#-- NAME as name , NODE_TYPE -->
        <#if (item_index > 0) >, </#if>${item.tableColumnToJavaSelect}
</#list>
    </sql>

    <!--    定义对象插入序列 值 无id自增-->
    <sql id="Base_Column_List_INSERT_VALUES">
<#list columnList as item>
        <#-- #{object.nodeCode} , #{object.calc} -->
        <#if (item_index > 0) >, </#if> <#noparse>#</#noparse>{object.${item.instanseName}}
</#list>
    </sql>

<!--    定义对象匹配规则 模糊查询  findPage  and  count  -->
    <sql id="readWhere">
<#--
<if test="object.nodeCode != null  and object.nodeCode !=''  ">
    AND NODE_CODE like "%"#{object.nodeCode}"%"
</if>
<if test="object.calc != null  ">
    AND CALC = #{object.calc}
</if>
<if test="object.calcBegin != null  ">
    AND CALC >= #{object.calcBegin}
</if>
<if test="object.calcEnd != null  ">
    AND CALC &lt; #{object.calcEnd}
</if>
<if test="object.localCodes !=null and object.localCodes.length > 0 and object.localCodes[0] != ''">
    AND ID IN
    <foreach collection="localCodes" item="string" open="(" separator="," close=")">
        #{string}
</foreach>
</if>
-->
<#list columnList as item>
<#if item.xmlLike?starts_with("0")>
<#--默认只有字符串才 like-->
        <if test="object.${item.instanseName} != null  and object.${item.instanseName} !=''  ">
            AND ${item.tableColumnName} like "%"<#noparse>#</#noparse>{object.${item.instanseName}}"%"
        </if>
<#elseif item.xmlLike?starts_with("1")>
        <if test="object.${item.instanseName} != null ">
            AND ${item.tableColumnName} = <#noparse>#</#noparse>{object.${item.instanseName}}
        </if>
<#else>
<#--不可查询-->
</#if>
<#if item.xmlDeta?starts_with("1")>
<#--TIMESTAMP查询问题 是否需要显示格式化 云数据库utc问题?-->
        <if test="object.${item.instanseName}Begin != null  ">
            AND ${item.tableColumnName} >= <#noparse>#</#noparse>{object.${item.instanseName}Begin}
        </if>
        <if test="object.${item.instanseName}End != null  ">
            AND ${item.tableColumnName} &lt; <#noparse>#</#noparse>{object.${item.instanseName}End}
        </if>
</#if>
<#if item.xmlIn?starts_with("1")>
        <if test="object.${item.instanseName}s !=null and object.${item.instanseName}s.length > 0 and object.${item.instanseName}s[0] != ''">
            AND ${item.tableColumnName} IN
            <foreach collection="object.${item.instanseName}s" item="arrItem" open="(" separator="," close=")">
                <#noparse>#</#noparse>{arrItem}
            </foreach>
        </if>
</#if>
</#list>

    </sql>

<#if isView?starts_with("0") >
<!--	Integer adds(List<HelloWorld> list);	 -->
    <insert id ="${adds}"  >
        insert into <include refid="Table" /> (
            <include refid="Base_Column_List" />
        ) values
        <foreach collection ="list" item="object" index= "index" separator =",">(
            <include refid="Base_Column_List_INSERT_VALUES" />
        )
    </foreach >
    </insert >

<!--	Integer update(@Param("object") HelloWorld object, @Param("id") String id);	 -->
    <update id="${update}" >
        update <include refid="Table" />
        <set>
<#list columnList as item>
        <#-- #{object.nodeCode}, #{object.calc}    -->
            <#if (item_index > 0) >, </#if>${item.tableColumnName} = <#noparse>#{object.</#noparse>${item.instanseName}}
</#list>
        </set>
        where ${primaryKey.tableColumnName} = <#noparse>#{id}</#noparse>
    </update>

<!--	Integer deletes(List<String>  list);-->
    <delete id="${deletes}"  >
        delete from <include refid="Table" />
        <where>
            ${primaryKey.tableColumnName} in
            <foreach collection="list" index="index" item="item" open="(" separator="," close=")">
                <#noparse>#</#noparse>{item}
            </foreach>
        </where>
    </delete>
<!--	Integer  deletesByObject(@Param("object") HelloWorld obj);	-->
    <delete id="${deletesByObject}" >
        delete from <include refid="Table" />
        <where>
            <include refid="readWhere" />
        </where>
    </delete>
</#if>

<!--	Long count(@Param("object") HelloWorld object);  java.lang.Integer-->
    <select id="count" resultType="${Long}">
        select count(*) from
        <include refid="Table" />
        <where>
            <include refid="readWhere" />
        </where>
    </select>

<!--	List<HelloWorld>  finds(@Param("object") HelloWorld obj, @Param("pager") pager);	-->
    <select id="${finds}"  resultMap="BaseResultMap"  >
        select <include refid="Base_Column_List" />
        from <include refid="Table" />
        <where>
            <include refid="readWhere" />
        </where>
        <if test="pager != null ">
            <if test="pager.orderBy != null and pager.orderBy != '' ">
                order by <#noparse>$</#noparse>{pager.orderBy}
            </if>
            <if test="pager.startPosition != null and pager.pageSize != null ">
                limit <#noparse>#{pager.startPosition}, #{pager.pageSize}</#noparse>
            </if>
        </if>
    </select>


</mapper>


