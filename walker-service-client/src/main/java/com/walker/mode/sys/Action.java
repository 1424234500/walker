package com.walker.mode.sys;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * jpa实体类 周期任务 子任务列表 周期执行sql 访问url  调用class
 */
@Entity
@Table(name = "W_ACTION")
public class Action implements Cloneable,Serializable {

    @Id     //主键
//    @GeneratedValue(strategy = GenerationType.AUTO)     //自增
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "ID", columnDefinition = "varchar(32) default '' comment '主键' ")
    private String ID;
    @Column(name = "S_MTIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
    private String S_MTIME;
//    @SqlColumn(name = "S_ATIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '添加时间' ")
//    private String S_ATIME;
    @Column(name = "S_FLAG", columnDefinition = "varchar(4) default '0' comment '1是0否' ")
    private String S_FLAG;


    @Column(name = "NAME", columnDefinition = "varchar(512) default 'name' comment '名字' ")    //255
    private String NAME;
    @Column(name = "ABOUT", columnDefinition = "varchar(998) default '' comment '说明' ")
    private String ABOUT;

    @Column(name = "TYPE", columnDefinition = "varchar(64) default '' comment '类型sql-class-url' ")
    private String TYPE;



    @Column(name = "VALUE", columnDefinition = "varchar(998) default '' comment '值' ")
    private String VALUE;


    @Override
    public String toString() {
        return "Action{" +
                "ID='" + ID + '\'' +
                ", S_MTIME='" + S_MTIME + '\'' +
                ", S_FLAG='" + S_FLAG + '\'' +
                ", NAME='" + NAME + '\'' +
                ", ABOUT='" + ABOUT + '\'' +
                ", TYPE='" + TYPE + '\'' +
                ", VALUE='" + VALUE + '\'' +
                '}';
    }

    public String getID() {
        return ID;
    }

    public Action setID(String ID) {
        this.ID = ID;
        return this;
    }

    public String getS_MTIME() {
        return S_MTIME;
    }

    public Action setS_MTIME(String S_MTIME) {
        this.S_MTIME = S_MTIME;
        return this;
    }

    public String getS_FLAG() {
        return S_FLAG;
    }

    public Action setS_FLAG(String S_FLAG) {
        this.S_FLAG = S_FLAG;
        return this;
    }

    public String getNAME() {
        return NAME;
    }

    public Action setNAME(String NAME) {
        this.NAME = NAME;
        return this;
    }

    public String getABOUT() {
        return ABOUT;
    }

    public Action setABOUT(String ABOUT) {
        this.ABOUT = ABOUT;
        return this;
    }

    public String getTYPE() {
        return TYPE;
    }

    public Action setTYPE(String TYPE) {
        this.TYPE = TYPE;
        return this;
    }

    public String getVALUE() {
        return VALUE;
    }

    public Action setVALUE(String VALUE) {
        this.VALUE = VALUE;
        return this;
    }
}