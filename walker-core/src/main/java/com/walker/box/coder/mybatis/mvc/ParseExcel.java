package com.walker.box.coder.mybatis.mvc;

import com.walker.box.coder.CodeUtil;

import java.util.List;

public class ParseExcel {


    public static MvcModel parseExcelList(MvcModel mvcModel, List<List<String>> list) {
        if(mvcModel == null) {
            mvcModel = new MvcModel();
        }
        if(list.get(0).get(0).startsWith("表名") && list.get(2).get(0).startsWith("字段名")){
            int cc = 0;
            mvcModel.setTableName(CodeUtil.getList(list.get(1), cc++, ""));
            mvcModel.setTableNameChinese(CodeUtil.getList(list.get(1), cc++, ""));
            mvcModel.setTableInfo(CodeUtil.getList(list.get(1), cc++, ""));
            mvcModel.setTablePre(CodeUtil.getList(list.get(1), cc++, mvcModel.getTableName().split("_")[0] + "_"));
            mvcModel.setIsView(CodeUtil.getList(list.get(1), cc++, ""));
            mvcModel.setIsExport(CodeUtil.getList(list.get(1), cc++, "0"));
            mvcModel.setIsSwagger(CodeUtil.getList(list.get(1), cc++, "0"));
            mvcModel.setIsMapperAnno(CodeUtil.getList(list.get(1), cc++, "0"));

//			读取解析行约束
            for(int i = 3; i < list.size(); i++){
                if(list.get(i) != null && list.get(i).size() > 0 && list.get(i).get(0) != null && list.get(i).get(0).length() > 0) {
                    Column column = ParseExcel.parseLineColumn(list.get(i));
                    if(column.isTableColumn.equals("0")){
                        mvcModel.getColumnList().add(column);
                    }
                    mvcModel.getColumnAll().add(column);

//					优先级逻辑主键
                    if(mvcModel.getPrimaryKey() == null || column.getNotNull().compareTo(mvcModel.getPrimaryKey().getNotNull()) > 0){
                        mvcModel.setPrimaryKey(column);
                    }
                }
            }

        }else{
            throw new RuntimeException("excel no parse error ? ");
        }

//		  构造字段
        mvcModel.init();
        //java sql查询模式
        if( ! mvcModel.getIsView().equals("2")) {
            mvcModel.createTable();
        }
        return mvcModel;
    }



    public static Column parseLineColumn(List<String> line){
        Column column = new Column();
        int i = 0;
        column.setTableColumnName(CodeUtil.getList(line, i++, ""));
        if(column.getTableColumnName().length() == 0) throw  new RuntimeException("不能空");
        column.setTableColumnType(CodeUtil.getList(line, i++, "").toUpperCase());
        if(column.getTableColumnType().length() == 0) throw  new RuntimeException("不能空");
        column.setNotNull((CodeUtil.getList(line, i++, "0")));
        column.setDefValue((CodeUtil.getList(line, i++, "")));
        column.setQueryEg((CodeUtil.getList(line, i++, "")));
        column.setQueryEg2((CodeUtil.getList(line, i++, "")));
        column.setNameChinese((CodeUtil.getList(line, i++, "")));
        column.setInfo((CodeUtil.getList(line, i++, "")));
        column.setMustQuery((CodeUtil.getList(line, i++, "0")));
        column.setXmlLike((CodeUtil.getList(line, i++, "0")));
        column.setXmlDeta((CodeUtil.getList(line, i++, "0")));
        column.setXmlIn((CodeUtil.getList(line, i++, "0")));
        column.setIsTableColumn((CodeUtil.getList(line, i++, "0")));

        column.init();
        return column;
    }




}
