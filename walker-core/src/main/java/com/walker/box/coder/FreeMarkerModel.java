package com.walker.box.coder;

import com.walker.core.util.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.Locale;


/**
 * new FreeModel("dir").make(map, "xxx.ftl", "xxx.md");
 */
public class FreeMarkerModel {

    String dirLoad;

    Configuration configuration;

    public FreeMarkerModel(String dirLoad) throws IOException {
        this.dirLoad = dirLoad;
        // step1 创建freeMarker配置实例
        configuration = new Configuration();
        // step2 获取模版路径
        configuration.setDirectoryForTemplateLoading(new File(dirLoad));
//            configuration.setAutoImports(map);
//            configuration.setAutoIncludes();
        configuration.setAutoFlush(true);
        configuration.setEncoding(Locale.ENGLISH, "utf-8");
    }

    public FreeMarkerModel make(Object dataModel, String filenameFtl, String filemake) throws Exception {
        Writer out = null;
        try {
            // step4 加载模版文件
            Template template = configuration.getTemplate("" + filenameFtl);
            // step5 生成数据
            FileUtil.mkdir(FileUtil.getFilePath(filemake));
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filemake)));
            // step6 输出文件
            template.process(dataModel, out);
        }finally {
            try {
                if (null != out) {
                    out.flush();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        return this;
    }

}