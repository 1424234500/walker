package com.walker.util;

import com.walker.core.encode.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 获取jar包版本号
 */
public final class Version {

    private static final Pattern VERSION_PATTERN = Pattern.compile("([0-9][0-9\\.\\-]*)\\.jar");
    protected static Logger logger = LoggerFactory.getLogger(XmlUtil.class);
    private static final String VERSION = getVersion(Version.class, "2.0.0");

    private Version() {
    }

    public static String getVersion() {
        return VERSION;
    }

    public static String getVersion(Class cls, String defaultVersion) {
        try {
            // 首先查找MANIFEST.MF规范中的版本号  
            String version = cls.getPackage().getImplementationVersion();
            if (version == null || version.length() == 0) {
                version = cls.getPackage().getSpecificationVersion();
            }
            if (version == null || version.length() == 0) {
                // 如果MANIFEST.MF规范中没有版本号，基于jar包名获取版本号  
                String file = cls.getProtectionDomain().getCodeSource().getLocation().getFile();
                if (file != null && file.length() > 0 && file.endsWith(".jar")) {
                    Matcher matcher = VERSION_PATTERN.matcher(file);
                    while (matcher.find() && matcher.groupCount() > 0) {
                        version = matcher.group(1);
                    }
                }
            }
            // 返回版本号，如果为空返回缺省版本号  
            return version == null || version.length() == 0 ? defaultVersion : version;
        } catch (Throwable e) { // 防御性容错  
            // 忽略异常，返回缺省版本号  
            logger.error(e.getMessage(), e);
            return defaultVersion;
        }
    }

}
