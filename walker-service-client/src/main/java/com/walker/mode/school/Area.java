package com.walker.mode.school;


import com.walker.service.Config;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * jpa实体类 地理信息 省市县区村
 */
@Entity
@Table(name = "W_AREA"
        , indexes = {
//                @Index(name = "INDEX_W_AREA_PATH", columnList = "PATH"),      //索引不能超过1000
//                @Index(name = "INDEX_W_AREA_PATH_NAME", columnList = "PATH_NAME"),
                @Index(name = "INDEX_W_AREA_P_ID", columnList = "P_ID")
            }
        )
public class Area implements Cloneable,Serializable {

    /**
     * 无需存表
     */
    @Transient
    String url;
    @Transient
    List<Area> childs = new ArrayList<>();
    public List<Area> addChilds(List<Area> childs){
        this.childs.addAll(childs);
        return childs;
    }
    public String getUrl() {
        return url;
    }
    public Area setUrl(String url) {
        this.url = url;
        return this;
    }
    public List<Area> getChilds() {
        return childs;
    }
    public Area setChilds(List<Area> childs) {
        this.childs = childs;
        return this;
    }



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
    @Column(name = "P_ID", columnDefinition = "varchar(32) default '' comment '上级' ")
    private String P_ID;
    @Column(name = "PATH", columnDefinition = "varchar(512) default '' comment '机构树-编码' ")
    private String PATH;
    @Column(name = "PATH_NAME", columnDefinition = "varchar(998) default '' comment '机构树-中文' ")
    private String PATH_NAME;

    @Column(name = "CODE", columnDefinition = "varchar(32) default '' comment 'code' ")
    private String CODE;

    @Column(name = "LEVEL", columnDefinition = "varchar(4) default '' comment '级别 深度' ")
    private String LEVEL;


    /**
     * 前缀标识  Dept Area User
     */
    public final static String prefix = "A_";


    public String getID() {
        return ID;
    }

    public Area setID(String ID) {
        ID = Config.makePrefix(prefix, ID);
        this.ID = ID;
        return this;
    }

    public String getS_MTIME() {
        return S_MTIME;
    }

    public Area setS_MTIME(String s_MTIME) {
        S_MTIME = s_MTIME;
        return this;
    }

    public String getS_FLAG() {
        return S_FLAG;
    }

    public Area setS_FLAG(String s_FLAG) {
        S_FLAG = s_FLAG;
        return this;
    }

    public String getNAME() {
        return NAME;
    }

    public Area setNAME(String NAME) {
        this.NAME = NAME;
        return this;
    }

    public String getP_ID() {
        return P_ID;
    }

    public Area setP_ID(String P_ID) {
        P_ID = Config.makePrefix(prefix, P_ID);

        this.P_ID = P_ID;
        return this;
    }

    public String getPATH() {
        return PATH;
    }

    public Area setPATH(String PATH) {
        this.PATH = PATH;
        return this;
    }

    public String getPATH_NAME() {
        return PATH_NAME;
    }

    public Area setPATH_NAME(String PATH_NAME) {
        this.PATH_NAME = PATH_NAME;
        return this;
    }

    public String getCODE() {
        return CODE;
    }

    public Area setCODE(String CODE) {
        this.CODE = CODE;
        return this;
    }

    public String getLEVEL() {
        return LEVEL;
    }

    public Area setLEVEL(String LEVEL) {
        this.LEVEL = LEVEL;
        return this;
    }

    @Override
    public String toString() {
        return "Area{" +
                "ID='" + ID + '\'' +
                ", S_MTIME='" + S_MTIME + '\'' +
                ", S_FLAG='" + S_FLAG + '\'' +
                ", NAME='" + NAME + '\'' +
                ", P_ID='" + P_ID + '\'' +
                ", PATH='" + PATH + '\'' +
                ", PATH_NAME='" + PATH_NAME + '\'' +
                ", CODE='" + CODE + '\'' +
                ", LEVEL='" + LEVEL + '\'' +
                '}';
    }
}