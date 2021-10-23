package com.walker.config;

import com.walker.ApplicationProviderTests;
import com.walker.util.Tools;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class DataSourceConfigTest extends ApplicationProviderTests {
    @Autowired
    @Qualifier("dataSource")
    DataSource dataSource;
    @Autowired
    @Qualifier("shardingDataSource")
    DataSource shardingDataSource;


    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    @Qualifier("shardingJdbcTemplate")  //同上都是同一个数据源同一个template
    JdbcTemplate shardingJdbcTemplate;



    @Test
    public void test(){
        try {
            Tools.out("walker normarl ds");
            Tools.out(dataSource);
            Tools.out(jdbcTemplate);
            jdbcTemplate.execute("delete from TEACHER where id=id");
            jdbcTemplate.execute("insert into TEACHER values('id','name','pwd','time' ) ");
            Tools.out(jdbcTemplate.queryForList("select * from TEACHER where id=id"));

            Tools.out("walker0,walker1 sharding ds");
            Tools.out(shardingDataSource);
            Tools.out(shardingJdbcTemplate);
            shardingJdbcTemplate.execute("delete from TEACHER where id=id1");
            shardingJdbcTemplate.execute("insert into TEACHER values('id1','name','pwd','time' ) ");
            Tools.out(shardingJdbcTemplate.queryForList("select * from TEACHER where id=id1"));

        }catch (Exception e){
            e.printStackTrace();
        }




    }


}