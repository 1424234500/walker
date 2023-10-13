<#--不常修改-->
<#assign localIpPortPre="http://10.33.77.117:8080/vehicleService/rest">
<#assign localIpPort="http://10.33.77.117:8080">
<#assign localPre="/vehicleService/rest">

<#--定义mapper service controller 函数名 接口名-->
<#assign adds="adds">
<#assign update="update">
<#assign deletes="deletes">
<#assign deletesByObject="deletesByObject">
<#assign count="count">
<#assign finds="finds">

<#--定义逻辑主键通用名为id or testCode -->
<#assign id="${primaryKey.instanseName}">
<#--定义模型别名为object or helloWorld   mapper xml 依旧使用object -->
<#assign object="${packageModel.instanseName}">
<#--数据操作数量返回Long or Integer ?-->
<#assign Long="Long">





<#assign localPackageBase="com.dahua.saas.base.*;">
<#assign localPackagePager="com.dahua.saas.base.Pager;">
<#assign localPackageJson="com.alibaba.fastjson.*;">

<#--log 4j slf 4j -->
<#assign localPackageSlf4j="org.slf4j.*;">
<#macro slf4j className >    private static final Logger log = LoggerFactory.getLogger(${className}.class);</#macro>





