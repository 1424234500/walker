package com.walker.dao;

import com.walker.core.database.BaseDaoAdapter;
import com.walker.core.database.SqlUtil;
import com.walker.util.Watch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/*
* 注解	含义
@Component	最普通的组件，可以被注入到spring容器进行管理
@Repository	作用于持久层
@Service	作用于业务逻辑层
@Controller	作用于表现层（spring-mvc的注解）
* */
@Repository
public class JdbcDao extends BaseDaoAdapter {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public JdbcDao(){
//        log.info(jdbcTemplate.getDataSource().toString());


    }

    @Override
    public String getDs() {
        return "mysql"; // jdbcTemplate.getDataSource().get
    }

    @Override
    public List<Map<String, Object>> find(String sql, Object... params) {
        List<Map<String, Object>> res = null;
        Watch w = new Watch(SqlUtil.makeSql(sql, params));
        try {
            res = jdbcTemplate.queryForList(sql, params);
            w.res(res, log);
        }catch (Exception e){
            w.exceptionWithThrow(e, log);
        }
        return res;
    }

    @Override
    public Integer executeSql(String sql, Object... params) {
        Integer res = null;
        Watch w = new Watch(SqlUtil.makeSql(sql, params));
        try {
            res = jdbcTemplate.update(sql, params);
            w.res(res, log);
        }catch (Exception e){
            w.exceptionWithThrow(e, log);
        }
        return res;
    }

    @Override
    public Integer[] executeSqlBatch(String sql, List<List<Object>> objs) {
        Watch w = new Watch(sql).put("size", objs.size()).put("eg", objs.get(0));
        Integer[] res = {};
        try {
            List<Object[]> args = new ArrayList<>();
            for(List<Object> arg : objs){
                args.add(arg.toArray());
            }
            int resint[] = jdbcTemplate.batchUpdate(sql, args);
            res = new Integer[resint.length];
            for(int i = 0; i< resint.length; i++){
                res[i] = resint[i];
            }
            w.res(Arrays.toString(res), log);
        } catch (Exception e) {
            w.exceptionWithThrow(e, log);
        }
        return res;
    }

    @Override
    public Integer executeProc(String proc, Object... objects) {
        return 0;
    }
}
