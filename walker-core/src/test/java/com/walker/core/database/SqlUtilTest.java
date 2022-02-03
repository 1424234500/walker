package com.walker.core.database;

import com.walker.mode.Bean;
import com.walker.util.Tools;
import org.junit.Test;

import java.util.Map;

public class SqlUtilTest {

	@Test
	public void test() {
	
	}

    @Test
    public void makeSqlCreate() {
	    Map bean = new Bean().set("id", "1234").set("name", "2323");
        Tools.out(SqlUtil.makeSqlCreate("hell", bean));
        Tools.out(SqlUtil.makeSqlInsert("hell", bean.keySet()));
    }
}
