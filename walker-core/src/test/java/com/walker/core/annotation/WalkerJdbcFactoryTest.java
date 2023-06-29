package com.walker.core.annotation;

import com.walker.core.database.Dao;
import com.walker.core.database.walkerjdbc.StudentWalkerJdbc;
import com.walker.core.mode.Page;
import com.walker.core.util.Tools;
import org.junit.Test;

public class WalkerJdbcFactoryTest {

    @Test
    public void getInstance() {
        Dao datasource = new Dao();
        StudentWalkerJdbc studentWalkerJdbc = WalkerJdbcFactory.getInstance(StudentWalkerJdbc.class, datasource);
        Tools.out(studentWalkerJdbc.updateNameById("2", "1"));

        Page page = new Page().setShownum(5).setNowpage(1);
        Tools.out(page);
        Tools.formatOut(studentWalkerJdbc.find(page, "%%"));
        Tools.out(page);

    }
}