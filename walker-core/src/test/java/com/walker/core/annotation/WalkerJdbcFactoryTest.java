package com.walker.core.annotation;

import com.walker.core.database.Dao;
import com.walker.core.database.walkerjdbc.StudentWalkerJdbc;
import com.walker.core.mode.Page;
import com.walker.util.Tools;
import org.junit.Test;

public class WalkerJdbcFactoryTest {

    @Test
    public void getInstance() {

        StudentWalkerJdbc studentWalkerJdbc = WalkerJdbcFactory
                .getInstance(StudentWalkerJdbc.class, new Dao().setDs("mysql"));
        Tools.out(studentWalkerJdbc.updateNameById("2", "1"));

        Page page = new Page().setShownum(5).setNowpage(1);
        Tools.out(page);
        Tools.formatOut(studentWalkerJdbc.find(page, "%%"));
        Tools.out(page);

    }
}