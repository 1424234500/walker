<#include "MvcBase.java.ftl" />
package ${packageService.packagePath};

import ${packageModel.packagePath}.*;
import java.util.*;
import ${localPackageBase}
import ${localPackagePager}

${infoClass}
public interface ${packageService.className} {
<#if isView?starts_with("0") >
    ${Long} ${adds}(List<${packageModel.className}> list);
    ${Long} ${update}(${packageModel.className} ${object}, String ${id});
    ${Long} ${deletes}(List<String>  list);
    ${Long} ${deletesByObject}(${packageModel.className} ${object});
</#if>
    ${Long} ${count}(${packageModel.className} ${object});
    List<${packageModel.className}>  ${finds}(${packageModel.className} ${object}, Pager pager);

}

