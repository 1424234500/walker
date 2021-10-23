# FreeMarker from Java

从 java 的角度编写 FreeMarker
从map键值一一替换模板变量生成代码

## !!! 用到的变量一定要模型传递 or 检测 null or 定义

注释 <#-- this is 注释 -->


```
# 包含文件
include "FreeMarkerUtil.ftl"
---
<#include "FreeMarkerUtil.ftl" />
---

# 导入文件成员变量 and 宏 函数
import "FreeMarkerUtil.ftl" as util
<#import "FreeMarkerUtil.ftl" as util>
util.name=${util.name}
util.sum(10,20)=<@util.sum a=10 b=20 />
<#--<#assign name="${util.name + " let by other"}" in util />-->

    # 定义 赋值 输出

    <#-- 定义绝对字符串? -->
    <#assign name1=r"网址(http:\www.baidu.com)">
    <#--避免转义-->
    <#noparse>#{id}</#noparse>

    #字符串
    <#assign nameMore=(name + "More")>
    <#assign name="HelloName">
    name=${name}
    name.toUpperFirst=${name?cap_first}
    name.toLowerFirst=${name?uncap_first}
    name.toLower=${name?lower_case}
    name.toUpper=${name?upper_case}
    name.charAt(2)=${name[2]}
    substr(0,5)=${name[0..5]}
    ${"Hello ${name} !"}
    ${"Hello " + name + " !"}


    ${"abcd"?starts_with("a")?string}<#--判断 字符串的第一个字符是什么，返回的是布尔值 翻译输出-->
    ${"abcaad"?index_of("a")}<#--判断 指定字符第一次出现的索引位置  0-->
    ${"abcda"?last_index_of("a")}<#--判断 指定字符最后一次出现的索引位置  4-->
    ${"abcd"?length}<#--返回字符串的长度  4-->
    ${"abcd"?left_pad(5,"1x")}<#--指定字符串的长度5 若字符串的长度<5 则向字符串的左侧插入指定的字符串，默认插入空格  1abcd-->
    ${"abcd"?right_pad(5,"1x")}<#--指定字符串的长度5 若字符串的长度<5 则向字符串的右侧插入指定的字符串，默认插入空格  abcd1-->
    ${"abcd"?contains("ac")?string}<#--判断字符串是否包含指定字符串 返回布尔值 需要处理-->
    ${"abcd"?replace("a","A")}<#--替换指定字符串  Abcd-->
    <#assign s="abcd"?split("c")><#-- 分割字符串  ab d-->

    <#list "张三,李思,,王强,柳树"?split(",") as name>
         "${name}"
    </#list>

    ${"   v abcd cc "?trim}<#--字符串去除字符串两端的空格  v abcd cc-->
    <#list " we are chinese you no diao"?word_list as word><#--以单词的 形式 分割字符串 we are chinese you no diao-->
        ${word}
    </#list>

    #浮点数
    floata=12.34
    <#assign floata=12.34>
    floata.intValue=${floata?int}

    #bool
    <#assign boola=false>
    boola=${boola?c}

    #时间
    timea=1602729583000
    <#assign timea=1602729583000>
    timea.format=${timea?string("yyyy-MM-dd HH:mm:ss")}

    # 算数运算 分支 循环 遍历
    inta=10
    <#assign inta=10>
    intb=5
    <#assign intb=5>
    inta+intb=${inta + intb}   # 支持"+"、"－"、"*"、"/"、"%"运算符

    # if 比较 gte lt
    <#if inta + intb gte 12 || inta - intb lt 6>
        inta + intb >= 12 || inta - intb < 6=true
    </#if>
<#--()注意-->
    <#if (name?length > 0) >
    </#if>
    <#--null问题默认值-->
    <#if name?starts_with("Hello")>
        name start with hello
    </#if>

    # if 判断 null
    <#if objectIf??>
        objectIf is not null=${objectIf}
    <#elseif objectIf??>
        objectIf elseif
    <#else>
        <#assign objectIf="new objectIf by FreeMarker">
        objectIf is null and new=${objectIf}
    </#if>

    # Map
    mapd={"name":"程序员", "salary":15000}
    <#assign mapd={"name":"程序员", "salary":15000}>
    mapd["name"]=${mapd["name"]}
    <#list mapd?keys as key>
        kv - ${key} ${mapd[key]}
    </#list>
    <#list mapd?values as value>
        v - ${value}
    </#list>
    
    # for List index sort sort_by("map.key") 反转 break
    listd=["L1", "L3", "L2"]
    <#assign listd=["L1", "L3", "L2"]>
    listd.size=${listd?size}
    <#list listd?sort?reverse as item>
        <#if (item_index > 0) >, </#if>
        ${item_index} ${item_has_next?c} ${item}
        <#if (item_index > 5) >
            <#break >
        </#if>
    </#list>


    # macro 宏
    <#macro macro1>
        无参数宏 macro ${name}
    </#macro>

    <#macro macro2 a b>宏 参数定义及调用需一一对应!!! ${a+b}</#macro>

    调用宏 macro2=<@macro2 a=1 b=2 />

```

