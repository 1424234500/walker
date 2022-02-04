package com.walker.mode.sys;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * jpa实体类 文件索引记录
 */
@Entity
@Table(name = "W_FILE_INDEX")
public class FileIndex implements Cloneable,Serializable{

    @Id     //主键
//    @GeneratedValue(strategy = GenerationType.AUTO)     //自增
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name="ID", columnDefinition = "varchar(32) default '' comment '主键' ")
    private String ID;
    @Column(name="CHECKSUM", columnDefinition = "varchar(32) default '' comment '校验和' ")
    private String CHECKSUM;


    @Column(name = "S_MTIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '修改时间' ")
    private String S_MTIME;
    @Column(name = "S_ATIME", columnDefinition = "varchar(32) default '1970-01-01 00:00:00' comment '添加时间' ")
    private String S_ATIME;
    @Column(name = "S_FLAG", columnDefinition = "varchar(4) default '0' comment '1是0否' ")
    private String S_FLAG;

    @Column(name = "NAME", columnDefinition = "varchar(512) default 'name' comment '文件名' ")
    private String NAME;
    @Column(name = "PATH", columnDefinition = "varchar(1024) default '' comment '存储路径' ") 
    private String PATH;
    @Column(name = "EXT", columnDefinition = "varchar(128) default '' comment '文件类型' ")
    private String EXT;
    @Column(name = "LENGTH", columnDefinition = "varchar(32) default '' comment '大小' ")
    private String LENGTH;

    @Column(name = "INFO", columnDefinition = "varchar(512) default '' comment '介绍' ")
    private String INFO;
    @Column(name = "OWNER", columnDefinition = "varchar(32) default '' comment '所属用户' ")
    private String OWNER;
    /**
     * 角色访问控制 继承上传所属用户级别
     */
    @Column(name = "LEVEL", columnDefinition = "varchar(128) default '' comment '权限级别' ")
    private String LEVEL;


    @Override
    public String toString() {
        return "FileIndex{" +
                "ID='" + ID + '\'' +
                ", CHECKSUM='" + CHECKSUM + '\'' +
                ", S_MTIME='" + S_MTIME + '\'' +
                ", S_ATIME='" + S_ATIME + '\'' +
                ", S_FLAG='" + S_FLAG + '\'' +
                ", NAME='" + NAME + '\'' +
                ", PATH='" + PATH + '\'' +
                ", EXT='" + EXT + '\'' +
                ", LENGTH='" + LENGTH + '\'' +
                ", INFO='" + INFO + '\'' +
                ", OWNER='" + OWNER + '\'' +
                ", LEVEL='" + LEVEL + '\'' +
                '}';
    }

    public String getCHECKSUM() {
        return CHECKSUM;
    }

    public FileIndex setCHECKSUM(String CHECKSUM) {
        this.CHECKSUM = CHECKSUM;
        return this;
    }
    public String getID() {
        return ID;
    }

    public FileIndex setID(String ID) {
        this.ID = ID;
        return this;
    }

    public String getS_MTIME() {
        return S_MTIME;
    }

    public FileIndex setS_MTIME(String s_MTIME) {
        S_MTIME = s_MTIME;
        return this;
    }

    public String getS_ATIME() {
        return S_ATIME;
    }

    public FileIndex setS_ATIME(String s_ATIME) {
        S_ATIME = s_ATIME;
        return this;
    }

    public String getS_FLAG() {
        return S_FLAG;
    }

    public FileIndex setS_FLAG(String s_FLAG) {
        S_FLAG = s_FLAG;
        return this;
    }

    public String getNAME() {
        return NAME;
    }

    public FileIndex setNAME(String NAME) {
        this.NAME = NAME;
        return this;
    }

    public String getPATH() {
        return PATH;
    }

    public FileIndex setPATH(String PATH) {
        this.PATH = PATH;
        return this;
    }

    public String getEXT() {
        return EXT;
    }

    public FileIndex setEXT(String EXT) {
        this.EXT = EXT;
        return this;
    }

    public String getLENGTH() {
        return LENGTH;
    }

    public FileIndex setLENGTH(String LENGTH) {
        this.LENGTH = LENGTH;
        return this;
    }

    public String getINFO() {
        return INFO;
    }

    public FileIndex setINFO(String INFO) {
        this.INFO = INFO;
        return this;
    }

    public String getOWNER() {
        return OWNER;
    }

    public FileIndex setOWNER(String OWNER) {
        this.OWNER = OWNER;
        return this;
    }

    public String getLEVEL() {
        return LEVEL;
    }

    public FileIndex setLEVEL(String LEVEL) {
        this.LEVEL = LEVEL;
        return this;
    }

}