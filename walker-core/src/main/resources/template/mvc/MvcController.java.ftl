<#include "MvcBase.java.ftl" />
package ${packageController.packagePath};

import ${packageModel.packagePath}.*;
import ${packageService.packagePath}.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.*;
import java.util.*;
<#if isExport?starts_with("0")>import com.dahua.saas.controller.ExportExcelBetaController;</#if>
<#if isSwagger?starts_with("0")>import io.swagger.annotations.*;</#if>
import ${localPackageBase}
import ${localPackageJson}
import ${localPackagePager}



${infoClass}
<#if isSwagger?starts_with("0")>@Api(tags = "${tableNameChinese} ${tableName} ${packageModel.className} /${packageModel.instanseName}", description = "${tableInfo}")</#if>
@RestController
@RequestMapping("/${packageModel.instanseName}")
public class ${packageController.className} <#if isExport?starts_with("0")>extends ExportExcelBetaController<Pager, ${packageModel.className}></#if>{

    @Autowired
    @Qualifier("${packageService.instanseName}")
    private ${packageService.className} ${packageService.instanseName};
<#if isView?starts_with("0") >
<#if isSwagger?starts_with("0")>    @ApiOperation(value = "批量添加 ", notes = "${tableNameChinese}")</#if>
    @ResponseBody
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public Object ${adds}(@RequestBody List<${packageModel.className}> list)  {
        ReturnInfoBean returnInfoBean = new ReturnInfoBean();
        ${Long} res = ${packageService.instanseName}.${adds}(list);
        returnInfoBean.setResults(res);
        if(res <= 0){
            returnInfoBean.setCodeAndMsg(ErrorCodeEnum.COMMON_ERROR);
        }
        return returnInfoBean;
    }

<#if isSwagger?starts_with("0")>    @ApiOperation(value = "根据逻辑主键${primaryKey.instanseName}更新 ", notes = "${tableNameChinese}")</#if>
    @ResponseBody
    @RequestMapping(value = "/{${primaryKey.instanseName}}", method = RequestMethod.PUT, produces = "application/json")
    public Object ${update}(@RequestBody ${packageModel.className} ${object}, @PathVariable(value = "${primaryKey.instanseName}", required = true) String ${primaryKey.instanseName} )  {
        ReturnInfoBean returnInfoBean = new ReturnInfoBean();
        Long res = ${packageService.instanseName}.${update}(${object}, ${primaryKey.instanseName});
        returnInfoBean.setResults(res);
        if(res <= 0){
            returnInfoBean.setCodeAndMsg(ErrorCodeEnum.COMMON_ERROR);
        }
        return returnInfoBean;
    }

<#if isSwagger?starts_with("0")>    @ApiOperation(value = "根据逻辑主键拼接 ${primaryKey.instanseName}s 批量删除 ", notes = "${tableNameChinese}")</#if>
    @ResponseBody
    @RequestMapping(value = "/{${id}s}", method = RequestMethod.DELETE, produces = "application/json")
    public Object ${deletes}(@PathVariable(value = "${primaryKey.instanseName}s", required = true) String  ${primaryKey.instanseName}s )  {
        ReturnInfoBean returnInfoBean = new ReturnInfoBean();
        Long res = ${packageService.instanseName}.${deletes}( Arrays.asList(${id}s.split(",") ) );
        returnInfoBean.setResults(res);
        if(res <= 0){
            returnInfoBean.setCodeAndMsg(ErrorCodeEnum.COMMON_ERROR);
        }
        return returnInfoBean;
    }

<#if isSwagger?starts_with("0")>    @ApiOperation(value = "动态查询命中批量删除 ", notes = "${tableNameChinese}")</#if>
    @ResponseBody
    @RequestMapping(value = "/${deletesByObject}", method = RequestMethod.DELETE, produces = "application/json")
    public Object ${deletesByObject}(@RequestBody ${packageModel.className} ${object} )  {
        ReturnInfoBean returnInfoBean = new ReturnInfoBean();
        Long res = ${packageService.instanseName}.${deletesByObject}( ${object} );
        returnInfoBean.setResults(res);
        if(res <= 0){
            returnInfoBean.setCodeAndMsg(ErrorCodeEnum.COMMON_ERROR);
        }
        return returnInfoBean;
    }
</#if>
<#if isSwagger?starts_with("0")>    @ApiOperation(value = "动态查询 ", notes = "${tableNameChinese}")</#if>
    @RequestMapping(value = "/${finds}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Object ${finds}(@RequestBody ${packageModel.className} ${object}, @RequestParam(value = "page", required = false, defaultValue = "1")  Integer page, @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, @RequestParam(value = "orderBy", required = false, defaultValue = "") String orderBy )  {
        ReturnInfoBean returnInfoBean = new ReturnInfoBean();
        Pager pager = new Pager();
        pager.setPage(page);
        pager.setPageSize(pageSize);
        pager.setOrderBy(orderBy);
        pager.initPager();
        List<${packageModel.className}> res = ${packageService.instanseName}.${finds}(${object}, pager);

        returnInfoBean.setResults(res);
        returnInfoBean.setTotalCount(pager.getTotalCount());
        return returnInfoBean;
    }

<#if isExport?starts_with("0")>
   @Override
    protected String getTmpData() {
        Map<String, String> map = new LinkedHashMap<>();

<#list columnList as item>
        <#--map.put("id", "主键自增");-->
        map.put("${item.instanseName}", "${item.nameChinese}");
</#list>
        return JSON.toJSONString(map);
    }

    @Override
    protected List<${packageModel.className}> findList(${packageModel.className} ${object}, Pager pager) {
        return ${packageService.instanseName}.${finds}(${object}, pager);
    }
</#if>

}


