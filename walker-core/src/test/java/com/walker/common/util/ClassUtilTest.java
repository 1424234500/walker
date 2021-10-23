package com.walker.common.util;

import com.walker.util.ClassUtil;
import com.walker.util.Tools;
import org.junit.Test;

import java.util.List;

public class ClassUtilTest {

    @Test
    public void getPackageClassBean() {
        List<?> list = ClassUtil.getPackageClassBean("", true);
        Tools.formatOut(list);
    }

    @Test
    public void getPackageClass() {
        List<?> list = ClassUtil.getPackageClass("", true);
        Tools.formatOut(list);


    }
}