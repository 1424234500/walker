package com.walker.service.impl;

import com.walker.core.database.SqlUtil;
import com.walker.core.mode.Page;
import com.walker.dao.JdbcTemplateDao;
import com.walker.mode.Teacher;
import com.walker.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service("teacherJdbcService")
public class TeacherJdbcServiceImpl implements TeacherService {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    JdbcTemplateDao jdbcTemplateDao;


    @Override
    public List<Teacher> saveAll(List<Teacher> teachers) {
        List<Teacher> res = new ArrayList<>();
        for (Teacher teacher : teachers) {
            List<Teacher> t = this.finds(teacher, new Page().setShownum(1));
            if (t.size() <= 0) {
                int i = jdbcTemplateDao.executeSql("INSERT INTO W_TEACHER(ID,LEVEL,NAME,S_ATIME,S_MTIME,S_FLAG,SEX) VALUES(?,?,?,?,?,?,?) ",
                        teacher.getID(),
                        teacher.getLEVEL(),
                        teacher.getNAME(),
                        teacher.getS_ATIME(),
                        teacher.getS_MTIME(),
                        teacher.getS_FLAG(),
                        teacher.getSEX()
                );
                if (i > 0) res.add(teacher);
            } else {
                int i = jdbcTemplateDao.executeSql("UPDATE TEACHER SET " +
                                "ID=?,LEVEL=?,NAME=?,S_ATIME=?,S_MTIME=?,S_FLAG=?,SEX=? WHERE ID=? ",
                        teacher.getID(),
                        teacher.getLEVEL(),
                        teacher.getNAME(),
                        teacher.getS_ATIME(),
                        teacher.getS_MTIME(),
                        teacher.getS_FLAG(),
                        teacher.getSEX(),
                        teacher.getID()
                );
                if (i > 0) res.add(teacher);
            }
        }
        return res;
    }

    @Override
    public Integer delete(Teacher test) {
        return jdbcTemplateDao.executeSql("DELETE FROM TEACHER WHERE ID=? ", test.getID());
    }

    @Override
    public Teacher get(Teacher test) {
        List<Teacher> list = jdbcTemplate.query("SELECT * FROM TEACHER WHERE ID=? OR NAME LIKE  CONCAT('%', ?, '%') ", new Object[]{test.getID(), test.getNAME()}, new BeanPropertyRowMapper<Teacher>(Teacher.class));
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<Teacher> finds(Teacher test, Page page) {
        String sql = "SELECT * FROM TEACHER WHERE ID=? " +
                "OR NAME LIKE  CONCAT('%', ?, '%') " + (page.getOrder().length() > 0 ? " ORDER BY " + page.getOrder().toUpperCase() : "");
        sql = SqlUtil.makeSqlPage(jdbcTemplateDao.getDs(), sql, page.getNowpage(), page.getShownum());
        List<Teacher> list = jdbcTemplate.query(
                sql,
                new Object[]{test.getID(), test.getNAME()},
                new BeanPropertyRowMapper<Teacher>(Teacher.class));
        return list;
    }

    @Override
    public Integer count(Teacher test) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM TEACHER WHERE ID=? OR NAME LIKE CONCAT('%', ?, '%')"
                , new Object[]{test.getID(), test.getNAME()}
                , Integer.class
        )
                ;
    }

    @Override
    public Integer[] deleteAll(List<String> ids) {
        int[] t = jdbcTemplate.batchUpdate("DELETE FROM TEACHER WHERE ID=? ", new BatchPreparedStatementSetter() {
            public int getBatchSize() {
                return ids.size();
            }

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, ids.get(i));
            }
        });
        Integer[] res = new Integer[t.length];
        for (int i = 0; i < t.length; i++) {
            res[i] = t[i];
        }
        return res;
    }

}
