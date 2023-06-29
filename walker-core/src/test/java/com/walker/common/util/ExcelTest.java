package com.walker.common.util;

import com.walker.TestBase;
import com.walker.core.util.Excel;
import com.walker.core.util.ExcelModel;
import org.junit.Test;

import java.util.Arrays;

public class ExcelTest extends TestBase {
    ExcelModel excelModel;
    @Override
    public void initData() {
        excelModel = new ExcelModel();
        excelModel.setSheetData("sheet1", listListString);
        excelModel.setSheetData("sheet2", listListString);
    }

    @Test
    public void save() {
        new Excel(fileNameTest + ".xlsx").save(excelModel);
        new Excel(fileNameTest + ".xlsx").save(ExcelModel.parse("hhh", Arrays.asList(Arrays.asList((Object)"aa", 11, 22))));

    }

    @Test
    public void read() {
        new Excel(fileNameTest + ".xlsx").save(excelModel);

        excelModel = new Excel(fileNameTest + ".xlsx").read();
        log.info(excelModel.toString());

    }

}