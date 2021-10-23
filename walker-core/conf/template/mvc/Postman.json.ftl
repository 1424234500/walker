<#include "MvcBase.java.ftl" />

{
	"variables": [],
	"info": {
		"name": "${packageModel.className}",
		"_postman_id": "40e2689a-${packageModel.className}",
		"description": "",
		"schema": "https://schema.getpostman.com/json/collection/v2.0.0/collection.json"
	},
	"item": [
<#if isView?starts_with("0") >
		{
			"name": "${localPre}/${packageModel.instanseName}",
			"request": {
				"url": "${localIpPort}${localPre}/${packageModel.instanseName}",
				"method": "POST",
				"header": [
					{
						"key": "Content-Type",
						"value": "application/json",
						"description": ""
					}
				],
				"body": {
					"mode": "raw",
					"raw": "[{\r\n<#list columnList as item>    <#if (item_index > 0) >, </#if>\"${item.instanseName}\" : <#if (item.queryEgNoQuoto?length > 0) >\"${item.queryEgNoQuoto}\"<#else>null</#if>\r\n</#list>}]\r\n"
                },
				"description": ""
			},
			"response": []
		},
		{
			"name": "${localPre}/${packageModel.instanseName}/{${primaryKey.instanseName}}",
			"request": {
				"url": "${localIpPort}${localPre}/${packageModel.instanseName}/<#if (primaryKey.queryEgNoQuoto?length > 0) >${primaryKey.queryEgNoQuoto}<#else>${primaryKey.instanseName}</#if>",
				"method": "PUT",
				"header": [
					{
						"key": "Content-Type",
						"value": "application/json",
						"description": ""
					}
				],
				"body": {
					"mode": "raw",
					"raw": "{\r\n<#list columnList as item>    <#if (item_index > 0) >, </#if>\"${item.instanseName}\" : <#if (item.queryEgNoQuoto?length > 0) >\"${item.queryEgNoQuoto}\"<#else>null</#if>\r\n</#list>}\r\n"
				},
				"description": ""
			},
			"response": []
		},
		{
			"name": "${localPre}/${packageModel.instanseName}/{${primaryKey.instanseName}s}",
			"request": {
				"url": "${localIpPort}${localPre}/${packageModel.instanseName}/<#if (primaryKey.queryEgNoQuoto?length > 0) >${primaryKey.queryEgNoQuoto},${primaryKey.queryEgNoQuoto}1,${primaryKey.queryEgNoQuoto}2,...<#else>${primaryKey.instanseName},${primaryKey.instanseName}1,${primaryKey.instanseName}2,...</#if>",
				"method": "DELETE",
				"header": [
					{
						"key": "Content-Type",
						"value": "application/json",
						"description": ""
					}
				],
				"body": {},
				"description": ""
			},
			"response": []
		},
		{
			"name": "${localPre}/${packageModel.instanseName}/deletesByObject",
			"request": {
				"url": "${localIpPort}${localPre}/${packageModel.instanseName}/deletesByObject",
				"method": "DELETE",
				"header": [
					{
						"key": "Content-Type",
						"value": "application/json",
						"description": ""
					}
				],
				"body": {
					"mode": "raw",
					"raw": "{\r\n    \"_\" : \"\"<#list columnList as item><#if item.xmlLike?starts_with("0")>\r\n    , \"${item.instanseName}\" : <#if (item.queryEgNoQuoto?length > 0) >\"${item.queryEgNoQuoto}\"<#else>null</#if><#elseif item.xmlLike?starts_with("1")>\r\n    , \"${item.instanseName}\" : <#if (item.queryEgNoQuoto?length > 0) >\"${item.queryEgNoQuoto}\"<#else>null</#if><#else></#if><#if item.xmlDeta?starts_with("1")>\r\n    , \"${item.instanseName}Begin\" : <#if (item.queryEgNoQuoto?length > 0) >\"${item.queryEgNoQuoto}\"<#else>null</#if>\r\n    , \"${item.instanseName}End\" : <#if (item.queryEgNoQuoto2?length > 0) >\"${item.queryEgNoQuoto2}\"<#else>null</#if></#if><#if item.xmlIn?starts_with("1")>\r\n    , \"${item.instanseName}s\" : [\"${item.queryEgNoQuoto}\", \"${item.queryEgNoQuoto}1\", \"${item.queryEgNoQuoto}2\", \"...\"]</#if></#list>\r\n}"
				},
				"description": ""
			},
			"response": []
		},
</#if>
		{
			"name": "${localPre}/${packageModel.instanseName}/finds?page={page}&pageSize={pageSize}&orderBy={orderBy}",
			"request": {
				"url": "${localIpPort}${localPre}/${packageModel.instanseName}/finds?page=1&pageSize=10&orderBy=${primaryKey.tableColumnName} DESC",
				"method": "POST",
				"header": [
					{
						"key": "Content-Type",
						"value": "application/json",
						"description": ""
					}
				],
				"body": {
					"mode": "raw",
					"raw": "{\r\n    \"_\" : \"\"<#list columnList as item><#if item.xmlLike?starts_with("0")>\r\n    , \"${item.instanseName}\" : <#if (item.queryEgNoQuoto?length > 0) >\"${item.queryEgNoQuoto}\"<#else>null</#if><#elseif item.xmlLike?starts_with("1")>\r\n    , \"${item.instanseName}\" : <#if (item.queryEgNoQuoto?length > 0) >\"${item.queryEgNoQuoto}\"<#else>null</#if><#else></#if><#if item.xmlDeta?starts_with("1")>\r\n    , \"${item.instanseName}Begin\" : <#if (item.queryEgNoQuoto?length > 0) >\"${item.queryEgNoQuoto}\"<#else>null</#if>\r\n    , \"${item.instanseName}End\" : <#if (item.queryEgNoQuoto2?length > 0) >\"${item.queryEgNoQuoto2}\"<#else>null</#if></#if><#if item.xmlIn?starts_with("1")>\r\n    , \"${item.instanseName}s\" : [\"${item.queryEgNoQuoto}\", \"${item.queryEgNoQuoto}1\", \"${item.queryEgNoQuoto}2\", \"...\"]</#if></#list>\r\n}"
				},
				"description": ""
			},
			"response": []
		}
	]
}