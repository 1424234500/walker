package com.walker.mode.sys;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * jpa实体类 用于mysql 连续日期区间问题
 */
@Entity
@Table(name = "W_BASE_DATE")
public class BaseDate implements Cloneable,Serializable {

    @Id     //主键
    @Column(name = "S_MTIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '年月日时分' ")
    private String S_MTIME;

    @Override
    public String toString() {
        return "BaseDate{" +
                "S_MTIME='" + S_MTIME + '\'' +
                '}';
    }

    public String getS_MTIME() {
        return S_MTIME;
    }

    public BaseDate setS_MTIME(String s_MTIME) {
        S_MTIME = s_MTIME;
        return this;
    }
}