package com.walker.box.coder;

import com.walker.util.FileUtil;
import lombok.Data;

import java.io.File;

@Data
public class CodeFileFtl {
    String ftlName = "MvcModel.java.ftl";
    String fileDir = "D:\\cph\\workspace\\saas-demo1\\tmd-domain\\src\\main\\java";
    String packagePath = "com.dahua.saas.tmd.domain.biz";

//    配置字段2 packageModel.instanseName
    String className;
    String instanseName;

//    加工字段
    String fileTo;  //生成文件 D:xxx/xxx.java / xml


    public CodeFileFtl(String ftlName, String fileDir, String packagePath) {
        this.ftlName = ftlName;
        this.fileDir = fileDir;
        this.packagePath = packagePath;
    }

    public void setName(String name) {
        this.className = CodeUtil.turnUpperFirst(name);
        this.instanseName = CodeUtil.turnLowerFirst(name);
    }

    public void init(){
        fileTo = fileDir    //d:/workspace
                + File.separator + CodeUtil.turnPackageToPath(packagePath)  //com.walker -> com/walker
                + File.separator + this.className   // HelloWorld
                + "." + FileUtil.getFileType(FileUtil.getFileNameOnly(ftlName)); //MvcMapper.java.ftl -> .java
        fileTo = fileTo.replace(File.separatorChar + File.separator, File.separator);
        FileUtil.mkdir(FileUtil.getFilePath(fileTo));
    }

}