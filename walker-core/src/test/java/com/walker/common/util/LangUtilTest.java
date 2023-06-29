package com.walker.common.util;

import com.walker.core.mode.Emp;
import com.walker.core.util.LangUtil;
import com.walker.core.util.Tools;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LangUtilTest {

    @Test
    public void beanToMap() {
        Emp emp = new Emp().setDept("dept").setId("id").setName("name");
        Tools.out(emp);
        Map<String, Object> map = LangUtil.turnObj2Map(emp);
        Tools.out(map);
        Emp nmp = LangUtil.turnMap2Obj(map, Emp.class);
        Tools.out(nmp);

        List<Emp> emps = Arrays.asList(emp, emp);
        Tools.out(emps);
        List<Map<String, Object>> maps = LangUtil.turnObj2MapList(emps);
        Tools.out(maps);
        List<Emp> nmps = LangUtil.turnMap2ObjList(maps, Emp.class);
        Tools.out(nmps);

    }

}