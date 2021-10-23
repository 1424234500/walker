package com.walker.mode;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * jpa实体类 角色关联用户/部门
 */
@Entity
@Table(name = "W_ROLE_USER")
public class RoleUser implements Cloneable,Serializable{

    @Id     //主键
//    @GeneratedValue(strategy = GenerationType.AUTO)     //自增
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name="ID", columnDefinition = "varchar(32) default '' comment '主键ROLE_USER' ")
    private String ID;
    @Column(name = "S_MTIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
    private String S_MTIME;
    @Column(name = "S_ATIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '添加时间' ")
    private String S_ATIME;
    @Column(name = "S_FLAG", columnDefinition = "varchar(4) default '0' comment '1是0否' ")
    private String S_FLAG;

    @Column(name = "USER_ID", columnDefinition = "varchar(32) default 'name' comment '用户/部门ID' ")    //255
    private String USER_ID;
    @Column(name = "ROLE_ID", columnDefinition = "varchar(32) default '0' comment '角色ID' ")
    private String ROLE_ID;

    public RoleUser(){

    }

    @Override
    public String toString() {
        return "RoleUser{" +
                "ID='" + ID + '\'' +
                ", S_MTIME='" + S_MTIME + '\'' +
                ", S_ATIME='" + S_ATIME + '\'' +
                ", S_FLAG='" + S_FLAG + '\'' +
                ", USER_ID='" + USER_ID + '\'' +
                ", ROLE_ID='" + ROLE_ID + '\'' +
                '}';
    }

    public String getID() {
        return ID;
    }

    public RoleUser setID(String ID) {
        this.ID = ID;
        return this;
    }

    public String getS_MTIME() {
        return S_MTIME;
    }

    public RoleUser setS_MTIME(String s_MTIME) {
        S_MTIME = s_MTIME;
        return this;
    }

    public String getS_ATIME() {
        return S_ATIME;
    }

    public RoleUser setS_ATIME(String s_ATIME) {
        S_ATIME = s_ATIME;
        return this;
    }

    public String getS_FLAG() {
        return S_FLAG;
    }

    public RoleUser setS_FLAG(String s_FLAG) {
        S_FLAG = s_FLAG;
        return this;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public RoleUser setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
        return this;
    }

    public String getROLE_ID() {
        return ROLE_ID;
    }

    public RoleUser setROLE_ID(String ROLE_ID) {
        this.ROLE_ID = ROLE_ID;
        return this;
    }
}