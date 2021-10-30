package com.walker.mode;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * jpa实体类 配置模型 redis db
 */
@Entity
@Table(name = "W_SYS_CONFIG")
public class SysConfig implements Cloneable,Serializable {
    @Id
    @Column(name="ID", columnDefinition = "varchar(300) default '' comment '键' ")
    private String ID;
    @Column(name = "S_MTIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
    private String S_MTIME;
    @Column(name = "S_FLAG", columnDefinition = "varchar(4) default '0' comment '1是0否' ")
    private String S_FLAG;

    @Column(name = "VALUE", columnDefinition = "varchar(512) default 'value' comment '值' ")    //255
    private String VALUE;
    @Column(name = "ABOUT", columnDefinition = "varchar(998) default '' comment '说明' ")
    private String ABOUT;

    @Override
    public String toString() {
        return "SysConfig{" +
                "ID='" + ID + '\'' +
                ", S_MTIME='" + S_MTIME + '\'' +
                ", S_FLAG='" + S_FLAG + '\'' +
                ", VALUE='" + VALUE + '\'' +
                ", ABOUT='" + ABOUT + '\'' +
                '}';
    }

    public String getID() {
        return ID;
    }

    public SysConfig setID(String ID) {
        this.ID = ID;
        return this;
    }

    public String getS_MTIME() {
        return S_MTIME;
    }

    public SysConfig setS_MTIME(String s_MTIME) {
        S_MTIME = s_MTIME;
        return this;
    }

    public String getS_FLAG() {
        return S_FLAG;
    }

    public SysConfig setS_FLAG(String s_FLAG) {
        S_FLAG = s_FLAG;
        return this;
    }

    public String getVALUE() {
        return VALUE;
    }

    public SysConfig setVALUE(String VALUE) {
        this.VALUE = VALUE;
        return this;
    }

    public String getABOUT() {
        return ABOUT;
    }

    public SysConfig setABOUT(String ABOUT) {
        this.ABOUT = ABOUT;
        return this;
    }
}