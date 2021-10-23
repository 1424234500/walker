package com.walker.box.coder.mybatis;

import com.walker.box.TaskThreadPie;
import com.walker.box.coder.CodeFileFtl;
import com.walker.box.coder.FreeMarkerModel;
import com.walker.box.coder.mybatis.mvc.MvcModel;
import com.walker.box.coder.mybatis.mvc.ParseExcel;
import com.walker.core.Context;
import com.walker.util.Excel;
import com.walker.util.FileUtil;
import com.walker.util.Tools;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.Data;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 根据配置(excel、注解)
 *
 * 生成 java 约束模型
 *
 * 结合 FreeMarker 模板
 *
 * 生成 mvc 各个类
 * 生成 接口文档 表sql 初始化数据sql
 * 生成 其他款式约束文件 excel、注解 java、及其他文档输出
 *
 *
 *
 */
@Data
public class MakeMvc {

    public MakeMvc()  {
    }

    String projectDir = "D:\\workspace\\walker-mk";
    String dirCopy = "D:\\doc\\makeCode";
    String docDir = dirCopy;

    String dirFromTemplate = Context.getPathConf("template/mybatis/mvc");
    String excelFile = Context.getPathConf("table-ftl-template.xlsx");


    public static void main(String[] args) throws Exception {
        Map<String, Object> bean = new HashMap<>();
        new MakeMvc().makeFilename(bean, Context.getPathConf("template/FreeMarker.ftl"), "FreeMarker.md");
        new MakeMvc().make();
    }

    public void makeFilename(Map<String, Object> bean, String ftlPath, String outFilePath){
        Writer out = null;
        try {
            // step1 创建freeMarker配置实例
            Configuration configuration = new Configuration();
            // step2 获取模版路径
            configuration.setDirectoryForTemplateLoading(new File(FileUtil.getFilePath(ftlPath)));
            //            configuration.setAutoImports(map);
//            configuration.setAutoIncludes();
            configuration.setAutoFlush(true);
            configuration.setEncoding(Locale.ENGLISH, "utf-8");
            // step4 加载模版文件
            Template template = configuration.getTemplate(FileUtil.getFileName(ftlPath));
            // step5 生成数据
            File docFile = new File(outFilePath);
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
            // step6 输出文件
            template.process(bean, out);
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^AutoCodeDemo.java 文件创建成功 !");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.flush();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    public void make() throws Exception {
        MvcModel mvcModel = new MvcModel();

//        绑定ftl 包 目标文件夹
        mvcModel.setPackageModel(new CodeFileFtl("MvcModel.java.ftl", projectDir + "\\src\\main\\java", "com.walker.model"));

        mvcModel.setPackageMapper(new CodeFileFtl("MvcMapper.java.ftl", projectDir + "\\src\\main\\java", "com.walker.dao"));
        mvcModel.setPackageMapperXml(new CodeFileFtl("MvcMapper.xml.ftl", projectDir + "\\src\\main\\resources", "mapper"));
        mvcModel.setPackageService(new CodeFileFtl("MvcService.java.ftl", projectDir + "\\src\\main\\java", "com.walker.service"));
        mvcModel.setPackageServiceImpl(new CodeFileFtl("MvcServiceImpl.java.ftl", projectDir + "\\src\\main\\java", "com.walker.service.impl"));

        mvcModel.setPackageController(new CodeFileFtl("MvcController.java.ftl", projectDir + "\\src\\main\\java", "com.walker.controller"));

        mvcModel.setPackageInterface(new CodeFileFtl("Interface.md.ftl", docDir, ""));
        mvcModel.setPackagePostman(new CodeFileFtl("Postman.json.ftl", docDir, ""));
        mvcModel.setPackageSql(new CodeFileFtl("DdlDml.sql.ftl", docDir, ""));

//        读取excel约束
        List<List<String>> list = new Excel(excelFile).read().getSheet(0);
        ParseExcel.parseExcelList(mvcModel, list);

        FileUtil.saveAs(mvcModel.getSqlDdlDml(), dirCopy + File.separator + mvcModel.getTableName() + ".sql", false);

        FreeMarkerModel freeMarkerModel = new FreeMarkerModel(dirFromTemplate);

        new TaskThreadPie(mvcModel.getClassNameToFtl().size()){
            @Override
            public void onStartThread(int threadNo) throws Exception {
                CodeFileFtl codeFileFtl = mvcModel.getClassNameToFtl().get(threadNo);
                Tools.out(codeFileFtl.getFtlName(), codeFileFtl.getFileTo());
                freeMarkerModel.make(mvcModel, codeFileFtl.getFtlName(), codeFileFtl.getFileTo());
                FileUtil.cp(codeFileFtl.getFileTo(), dirCopy + File.separator + FileUtil.getFileName(codeFileFtl.getFileTo()));
            }
        }.start();

        FileUtil.saveAs(mvcModel.getSqlDdlDml(), mvcModel.getPackageSql().getFileTo(), false);
        FileUtil.cp(mvcModel.getPackageSql().getFileTo(), dirCopy + File.separator + FileUtil.getFileName(mvcModel.getPackageSql().getFileTo()));



    }




}