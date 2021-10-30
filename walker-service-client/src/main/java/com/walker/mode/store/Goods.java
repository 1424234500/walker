package com.walker.mode.store;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 商品
 */
@Entity
@Table(name = "S_GOODS")
public class Goods implements Cloneable,Serializable {

    @Id     //主键
//    @GeneratedValue(strategy = GenerationType.AUTO)     //自增
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "ID", columnDefinition = "varchar(32) default '' comment '主键' ")
    private String ID;
    @Column(name = "S_MTIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
    private String S_MTIME;
    @Column(name = "S_ATIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '添加时间' ")
    private String S_ATIME;
    @Column(name = "S_FLAG", columnDefinition = "varchar(4) default '0' comment '1是0否' ")
    private String S_FLAG;


    @Column(name = "NAME", columnDefinition = "varchar(256) default 'name' comment '名字' ")    //255
    private String NAME;
    @Column(name = "ABOUT", columnDefinition = "varchar(998) default '' comment '说明' ")
    private String ABOUT;
    @Column(name = "IMGS", columnDefinition = "varchar(512) default '' comment '图片urls或ids' ")
    private String IMGS;
    @Column(name = "TYPE", columnDefinition = "varchar(64) default '' comment '类型' ")
    private String TYPE;


    @Column(name = "PRICE", columnDefinition = "varchar(16) default '998' comment '价格' ")
    private String PRICE;
    @Column(name = "WEIGHT", columnDefinition = "varchar(16) default '0kg' comment '重量' ")
    private String WEIGHT;

    @Column(name = "COMPANY", columnDefinition = "varchar(128) default 'Umbrella' comment '厂家' ")
    private String COMPANY;
    @Column(name = "PRODUCT_TIME", columnDefinition = "varchar(16) default '1970-01-01' comment '生产日期' ")
    private String PRODUCT_TIME;
    @Column(name = "SHELF_TIME", columnDefinition = "varchar(16) default '3month' comment '保质期' ")
    private String SHELF_TIME;

    @Override
    public String toString() {
        return "Goods{" +
                "ID='" + ID + '\'' +
                ", S_MTIME='" + S_MTIME + '\'' +
                ", S_ATIME='" + S_ATIME + '\'' +
                ", S_FLAG='" + S_FLAG + '\'' +
                ", NAME='" + NAME + '\'' +
                ", ABOUT='" + ABOUT + '\'' +
                ", IMGS='" + IMGS + '\'' +
                ", TYPE='" + TYPE + '\'' +
                ", PRICE='" + PRICE + '\'' +
                ", WEIGHT='" + WEIGHT + '\'' +
                ", COMPANY='" + COMPANY + '\'' +
                ", PRODUCT_TIME='" + PRODUCT_TIME + '\'' +
                ", SHELF_TIME='" + SHELF_TIME + '\'' +
                '}';
    }

    public String getID() {
        return ID;
    }

    public Goods setID(String ID) {
        this.ID = ID;
        return this;
    }

    public String getS_MTIME() {
        return S_MTIME;
    }

    public Goods setS_MTIME(String s_MTIME) {
        S_MTIME = s_MTIME;
        return this;
    }

    public String getS_ATIME() {
        return S_ATIME;
    }

    public Goods setS_ATIME(String s_ATIME) {
        S_ATIME = s_ATIME;
        return this;
    }

    public String getS_FLAG() {
        return S_FLAG;
    }

    public Goods setS_FLAG(String s_FLAG) {
        S_FLAG = s_FLAG;
        return this;
    }

    public String getNAME() {
        return NAME;
    }

    public Goods setNAME(String NAME) {
        this.NAME = NAME;
        return this;
    }

    public String getABOUT() {
        return ABOUT;
    }

    public Goods setABOUT(String ABOUT) {
        this.ABOUT = ABOUT;
        return this;
    }

    public String getIMGS() {
        return IMGS;
    }

    public Goods setIMGS(String IMGS) {
        this.IMGS = IMGS;
        return this;
    }

    public String getTYPE() {
        return TYPE;
    }

    public Goods setTYPE(String TYPE) {
        this.TYPE = TYPE;
        return this;
    }

    public String getPRICE() {
        return PRICE;
    }

    public Goods setPRICE(String PRICE) {
        this.PRICE = PRICE;
        return this;
    }

    public String getWEIGHT() {
        return WEIGHT;
    }

    public Goods setWEIGHT(String WEIGHT) {
        this.WEIGHT = WEIGHT;
        return this;
    }

    public String getCOMPANY() {
        return COMPANY;
    }

    public Goods setCOMPANY(String COMPANY) {
        this.COMPANY = COMPANY;
        return this;
    }

    public String getPRODUCT_TIME() {
        return PRODUCT_TIME;
    }

    public Goods setPRODUCT_TIME(String PRODUCT_TIME) {
        this.PRODUCT_TIME = PRODUCT_TIME;
        return this;
    }

    public String getSHELF_TIME() {
        return SHELF_TIME;
    }

    public Goods setSHELF_TIME(String SHELF_TIME) {
        this.SHELF_TIME = SHELF_TIME;
        return this;
    }
}