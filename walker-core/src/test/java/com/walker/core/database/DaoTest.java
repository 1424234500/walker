package com.walker.core.database;


import com.walker.core.encode.PinyinUtil;
import com.walker.util.FileUtil;
import com.walker.util.Tools;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DaoTest {

	@Test
	public void testPool(){
		PoolC3p0Mysql pool = PoolC3p0Mysql.getInstance();
		pool.setPwd("wxxxx");
		pool.setDriver("slsls.dsakfl.slsdf");
		Dao dao = new Dao(pool);
		Tools.formatOut(dao.find("SELECT * FROM DUAL LIMIT 1;"));
	}
	@Test
	public void testPoolSqlite(){
		PoolC3p0Mysql pool = PoolC3p0Mysql.getInstance();
		pool.setDriver("org.sqlite.JDBC");
//		pool.setUrl("jdbc:sqlite:/sqlite/walker");
		String file = "/db/walker.sqlite.db";
		FileUtil.mkfile(file);
		pool.setUrl("jdbc:sqlite:" + file);
		Dao dao = new Dao(pool);
		Tools.out(dao.executeSql("create table if not exists test(name text, age text)"));
		Tools.out(dao.executeSql("insert into test(name, age) values(?, ?) ", "name", "18"));
		Tools.out(dao.executeSql("insert into test(name, age) values(?, ?) ", "name1", "19"));

		Tools.formatOut(dao.find("SELECT * FROM test LIMIT 10 "));



		dao = new Dao(PoolC3p0Sqlite.getInstance());
		Tools.out(dao.executeSql("create table if not exists test(name text, age text)"));
		Tools.out(dao.executeSql("insert into test(name, age) values(?, ?) ", "name", "18"));
		Tools.out(dao.executeSql("insert into test(name, age) values(?, ?) ", "name1", "19"));

		Tools.formatOut(dao.find("SELECT * FROM test LIMIT 10 "));
	}


	@Test
	public void testGetColumnMap(){
		Tools.out(new Dao().getDatabasesOrUsers());
		Tools.out(new Dao().getTables(""));
		Tools.out(new Dao().getTables("walker"));
		Tools.out(new Dao().getColumnsByDbAndTable("", "W_TEACHER"));
		Tools.out(new Dao().getColumnsByDbAndTable("walker", "W_TEACHER"));
		Tools.out(new Dao().findPageRand(3, "select * from W_AREA"));
	}
	@Test
	public void testPo(){
		BaseDao  dao = new Dao();
		List<Object> list = new ArrayList<>();
//		list.add(new String[]{"0", "3"});
		list.add("0");
		list.add("3");
		Tools.out(dao.findPage("select * from TEACHER where ID in (?, ?) ", 1, 10,  list.toArray()));

		list.clear();
		list.add("ID ");
		Tools.out(dao.findPage("select * from TEACHER where 1=1 order by ? ", 1, 10,  list.toArray()));


	}
	@Test
	public void testEF() {
		BaseDao dao = new Dao();

		Tools.out(dao.executeSql("CREATE TABLE  IF NOT EXISTS  junit (code VARCHAR(20), name VARCHAR(20)); "));
		Tools.out(dao.getColumnsByDbAndTable("", "junit"));
		Tools.out( SqlUtil.toKeys(dao.find("select t.*, 'addCol' from junit t ")));
		for(int i = 0; i < 3; i++)
			Tools.out(dao.executeSql("insert into junit values(?,?)", i , PinyinUtil.getRandomName(1, null)));
		Tools.out(dao.count("select * from junit"));
		Tools.out(dao.find("select * from junit"));
		Tools.out(dao.findPage("select * from junit", 1, 3));
		Tools.out(dao.findPage("select * from junit", 2, 2));
		
		Tools.out(dao.executeSql("delete from junit  where code like ?", "%1%"));
		Tools.out(dao.executeSql("delete from junit  where code like ?", "%2%"));
		Tools.out(dao.executeSql("drop table junit"));

	}

	

}
