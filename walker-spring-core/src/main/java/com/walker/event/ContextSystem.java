package com.walker.event;

import com.walker.core.Context;
import com.walker.core.cache.ConfigMgr;

/**
 * 系统共用环境 结合缓存 统一接口 避免多处直接查询缓存 缓存设置值来源于 系统初始化 时加载的 配置文件 或者 数据库
 */
public class ContextSystem extends Context {

    final static public String defaultFileUploadDir = "/home/walker/tomcat";
    final static public String defaultFileDownloadDirs = "/home/walker/tomcat,/home/walker/help_note";

    static public String getUploadDir() {
        return ConfigMgr.getInstance().get("FILE_UPLOAD_DIR", defaultFileUploadDir);
    }

    static public String getScanDirs() {
        return ConfigMgr.getInstance().get("FILE_SCAN_DIR", defaultFileDownloadDirs);
    }

}
