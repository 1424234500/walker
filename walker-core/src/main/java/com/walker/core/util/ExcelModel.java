package com.walker.core.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ExcelModel {
    LinkedHashMap<String, List<List<String>>> datas = new LinkedHashMap<>();

    public static ExcelModel parse(List<List> listList) {
        return parse("sheetname", listList);
    }

    public static ExcelModel parse(String sheetName, List<List> listList) {
        ExcelModel excelModel = new ExcelModel();
        List<List<String>> listList1 = new ArrayList<>();
        for (List list : listList) {
            List<String> item = (List<String>) list.stream().map(itemItem -> String.valueOf(itemItem)).collect(Collectors.toList());
            listList1.add(item);
        }
        excelModel.setSheetData(sheetName, listList1);
        return excelModel;
    }

    public List<List<String>> setSheetData(String sheetName, List<List<String>> data) {
        return datas.put(sheetName, data);
    }

    public List<String> getSheetNames() {
        return new ArrayList<>(datas.keySet());
    }

    public String getSheetName(Integer sheetIndex) {
        List<String> sheetNames = getSheetNames();
        if (sheetIndex < 0 || sheetNames == null || sheetNames.size() <= sheetIndex) {
            throw new ArrayIndexOutOfBoundsException(sheetIndex);
        }
        return sheetNames.get(sheetIndex);
    }

    public List<List<String>> getSheet(Integer sheetIndex) {
        return getSheet(getSheetName(sheetIndex));
    }

    public List<List<String>> getSheet(String sheetName) {
        if (datas == null) {
            throw new ArrayIndexOutOfBoundsException(sheetName + " of null ");
        }
        return datas.get(sheetName);
    }

    public List<List<String>> getSheetAll() {
        List<List<String>> res = new ArrayList<>();
        for (String sheet : getSheetNames()) {
            res.addAll(getSheet(sheet));
        }
        return res;
    }

    public Integer size() {
        return datas.size();
    }

    @Override
    public String toString() {
        String res = "ExcelModel." + size() + " \n";
        for (int i = 0; i < size(); i++) {
            res += "\t" + i + ".\t" + getSheetName(i) + "\t" + getSheet(i).size() + "\n";
        }
        res += "";
        return res;
    }

}
