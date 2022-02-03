package com.walker.dao;

import com.walker.core.database.BaseDaoAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/*
* 注解	含义
@Component	最普通的组件，可以被注入到spring容器进行管理
@Repository	作用于持久层
@Service	作用于业务逻辑层
@Controller	作用于表现层（spring-mvc的注解）
* */
@Repository("jdbcTemplateDao")
public class JdbcTemplateDao extends BaseDaoAdapter {

    @Autowired
    JdbcTemplate jdbcTemplate;

    String ds = "mysql";

    @Override
    public String getDs() {
        return ds;
    }

    @Override
    public List<Map<String, Object>> find(String sql, Object... params) {
        return jdbcTemplate.queryForList(sql, params);
    }

    @Override
    public Integer executeSql(String sql, Object... params) {
        return jdbcTemplate.update(sql, params);
    }

    @Override
    public Integer[] executeSqlBatch(String sql, List<List<Object>> objs) {
        List<Object[]> args = new ArrayList<>();
        for (List<Object> arg : objs) {
            args.add(arg.toArray());
        }
        int[] resint = jdbcTemplate.batchUpdate(sql, args);
        Integer[] res = new Integer[resint.length];
        for (int i = 0; i < resint.length; i++) {
            res[i] = resint[i];
        }
        return res;
    }

    @Override
    public Integer executeProc(String proc, Object... objects) {
        return 0;
    }
}
