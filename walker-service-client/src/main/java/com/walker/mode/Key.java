package com.walker.mode;


/**
 * 静态键配置化
 * andorid
 * redis key
 */
public class Key {




    public static final String BEFORE = "BEFORE";
    public static final String COUNT = "COUNT";

    
    
    public static final String ALL_USER = "all_user";
    public static final String ALL_SOCKET = "all_socket";

    public static final String MSG = "MSG";
    public static final String NAME = "NAME";
    public static final String TYPE = "TYPE";
    public static final String TEXT = "TEXT";
    public static final String VOICE = "VOICE";
    public static final String FILE = "FILE";
    public static final String PHOTO = "PHOTO";
    
    public static final String PROFILE = "PROFILE";
    public static final String IMAGE = "IMAGE";
    public static final String PNG = "PNG";
    public static final String GRAPH = "GRAPH";

    public static final String ID = "ID";
    public static final String TIME = "TIME";
    public static final String NUM = "NUM";
    public static final String KEY = "KEY";

    public static final String SELF = "SELF";
    public static final String FROM = "FROM";
    public static final String TO = "TO";
    public static final String AUTO = "AUTO";
    public static final String DD = "dd";

	final public static String USER = "USER";	
	final public static String PWD = "PWD";	
	
	
	final public static String SESSION = "SESSION";	


	final public static String INFO = "INFO";	
	final public static String ABOUT = "ABOUT";	
	
	final public static String SOCKET = "SOCKET";	
	
	final public static String STA = "STA";

	/**
	 * 未操作
	 */
	final public static String STA_DEF = "-1";

	/**
	 * 下载中
	 */
	final public static String STA_LOADING = "1";
	/**
	 * 下载失败
	 */
	final public static String STA_FALSE = "0";	
	/**
	 * 已下载
	 */
	final public static String STA_TRUE = "101";


	/**
	 * 离线消息
	 * @param key
	 */
	public static String getKeyOffline(String key) {
		return "offline:msg:" + key;
	}


	/**
	 * cache锁
	 * @param key
	 */
	public static String getLockRedisCache(String key){
		return "lock:redis:cache:" + key;
	}

	/**
	 * 锁
	 * @param key
	 */
	public static String getLockRedis(String key){
		return "lock:" + key;//:	redis:make
	}
	/**
	 * url访问记录 zset
	 */
	public static String getUrlDone(){
		return "url:done";

	}

	/**
	 * 登录
	 */
	public static String getLoginToken(String key){
		return "login:token:" + key;

	}

	
	
}
