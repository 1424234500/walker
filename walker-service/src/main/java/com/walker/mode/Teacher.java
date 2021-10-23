package com.walker.mode;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * jpa实体类 学生
 */
@Entity
@Table(name = "W_TEACHER")
public class Teacher implements Cloneable,Serializable{

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
    @Column(name = "SEX", columnDefinition = "varchar(4) default '0' comment '1男0女' ")
    private String SEX;

    @Column(name = "LEVEL", columnDefinition = "varchar(16) default '0' comment '级别' ")
    private String LEVEL;

    
    public Teacher(){

    }

    @Override
    public String toString() {
        return "Teacher{" +
                "ID='" + ID + '\'' +
                ", S_MTIME='" + S_MTIME + '\'' +
                ", S_ATIME='" + S_ATIME + '\'' +
                ", S_FLAG='" + S_FLAG + '\'' +
                ", NAME='" + NAME + '\'' +
                ", SEX='" + SEX + '\'' +
                ", LEVEL='" + LEVEL + '\'' +
                '}';
    }

    public String getID() {
        return ID;
    }

    public Teacher setID(String ID) {
        this.ID = ID;
        return this;
    }

    public String getS_MTIME() {
        return S_MTIME;
    }

    public Teacher setS_MTIME(String S_MTIME) {
        this.S_MTIME = S_MTIME;
        return this;
    }

    public String getS_ATIME() {
        return S_ATIME;
    }

    public Teacher setS_ATIME(String S_ATIME) {
        this.S_ATIME = S_ATIME;
        return this;
    }

    public String getS_FLAG() {
        return S_FLAG;
    }

    public Teacher setS_FLAG(String s_FLAG) {
        S_FLAG = s_FLAG;
        return this;
    }

    public String getNAME() {
        return NAME;
    }

    public Teacher setNAME(String NAME) {
        this.NAME = NAME;
        return this;
    }

    public String getSEX() {
        return SEX;
    }

    public Teacher setSEX(String SEX) {
        this.SEX = SEX;
        return this;
    }

    public String getLEVEL() {
        return LEVEL;
    }

    public Teacher setLEVEL(String LEVEL) {
        this.LEVEL = LEVEL;
        return this;
    }
}