package com.walker.mode.school;


import com.walker.service.Config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * jpa实体类 角色
 */
@Entity
@Table(name = "W_ROLE")
public class Role implements Cloneable,Serializable{

    @Id     //主键
//    @GeneratedValue(strategy = GenerationType.AUTO)     //自增
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name="ID", columnDefinition = "varchar(32) default '' comment '主键' ")
    private String ID;
    @Column(name = "S_MTIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
    private String S_MTIME;
    @Column(name = "S_ATIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '添加时间' ")
    private String S_ATIME;
    @Column(name = "S_FLAG", columnDefinition = "varchar(4) default '0' comment '1是0否' ")
    private String S_FLAG;

    @Column(name = "NAME", columnDefinition = "varchar(512) default 'name' comment '名字' ")    //255
    private String NAME;
    /**
     * 最多分配给多少个用户
     */
    @Column(name = "NUM", columnDefinition = "varchar(32) default '998' comment '分配上限' ")
    private String NUM;
    /**
     * 角色级别 高级低级优先级
     */
    @Column(name = "LEVEL", columnDefinition = "varchar(32) default '0' comment '级别' ")
    private String LEVEL;

    @Override
    public String toString() {
        return "Role{" +
                "ID='" + ID + '\'' +
                ", S_MTIME='" + S_MTIME + '\'' +
                ", S_ATIME='" + S_ATIME + '\'' +
                ", S_FLAG='" + S_FLAG + '\'' +
                ", NAME='" + NAME + '\'' +
                ", NUM='" + NUM + '\'' +
                ", LEVEL='" + LEVEL + '\'' +
                '}';
    }

    /**
     * 前缀标识  Dept Area User
     */
    public final static String prefix = "R_";

    public String getID() {
        return ID;
    }

    public Role setID(String ID) {
        ID = Config.makePrefix(prefix, ID);

        this.ID = ID;
        return this;
    }

    public String getS_MTIME() {
        return S_MTIME;
    }

    public Role setS_MTIME(String s_MTIME) {
        S_MTIME = s_MTIME;
        return this;
    }

    public String getS_ATIME() {
        return S_ATIME;
    }

    public Role setS_ATIME(String s_ATIME) {
        S_ATIME = s_ATIME;
        return this;
    }

    public String getS_FLAG() {
        return S_FLAG;
    }

    public Role setS_FLAG(String s_FLAG) {
        S_FLAG = s_FLAG;
        return this;
    }

    public String getNAME() {
        return NAME;
    }

    public Role setNAME(String NAME) {
        this.NAME = NAME;
        return this;
    }

    public String getNUM() {
        return NUM;
    }

    public Role setNUM(String NUM) {
        this.NUM = NUM;
        return this;
    }

    public String getLEVEL() {
        return LEVEL;
    }

    public Role setLEVEL(String LEVEL) {
        this.LEVEL = LEVEL;
        return this;
    }
}