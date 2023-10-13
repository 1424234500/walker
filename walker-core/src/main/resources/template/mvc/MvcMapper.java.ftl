<#include "MvcBase.java.ftl" />
package ${packageMapper.packagePath};

import ${packageModel.packagePath}.*;
import java.util.*;
import org.apache.ibatis.annotations.*;
import ${localPackageBase}
import ${localPackagePager}




${infoClass}
<#if isMapperAnno?starts_with("0") >
@Mapper
</#if>
public interface ${packageMapper.className} {
<#if isView?starts_with("0") >
    ${Long} ${adds}(List<${packageModel.className}> list);
    ${Long} ${update}(@Param("object") ${packageModel.className} object, @Param("id") String ${id});
    ${Long} ${deletes}(List<String>  list);
    ${Long} ${deletesByObject}(@Param("object") ${packageModel.className} object);
</#if>

    ${Long} ${count}(@Param("object") ${packageModel.className} object);
    List<${packageModel.className}>  ${finds}(@Param("object") ${packageModel.className} object, @Param("pager") Pager pager);
}


