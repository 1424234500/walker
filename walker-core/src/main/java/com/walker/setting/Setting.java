package com.walker.setting;

import com.walker.core.Context;
import com.walker.core.exception.ExceptionUtil;
import com.walker.util.FileUtil;
import com.walker.util.LangUtil;
import com.walker.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件 读写工具
 * 针对某个properties文件的快捷实时存取
 */
public class Setting {

	public static Map<String, Object> getSetting(String filename)  {
		Map<String, Object> res = new LinkedHashMap<>();
		try {
			Properties proper = new Properties();
			InputStream in = new BufferedInputStream(new FileInputStream(filename));
			proper.load(in);
			in.close();
			Map<Object, Object> map = new LinkedHashMap<>(proper);
			map.forEach((key, value) -> {
				res.put(String.valueOf(key), value);
			});
		}catch (IOException e){
			ExceptionUtil.on(e);
		}

		return res;
	}
	
	
	public static String settingFileName = Context.getPathConf( "make.properties" );
	private static Properties proper ;
	static  {
		proper = new Properties();     
        try{
            //读取属性文件a.properties
        	FileUtil.mkfile(settingFileName);
            InputStream in = new BufferedInputStream (new FileInputStream(settingFileName));
            proper.load(in);     ///加载属性列表 
            System.out.println(proper.toString());
            in.close(); 
        }
        catch(Exception e){
            ExceptionUtil.on(e);
        }
	}
	
	public static String getProperty(String key, String defaultValue ){
		if(defaultValue == null) {
			defaultValue = "";
		}

		String res = proper.getProperty(key);
		if(StringUtils.isEmpty(res)){	//该键值不存在 则添加存入文件
			res = defaultValue;
		}
		saveProperty(key, defaultValue);
		return res;
	}
	public static <T> T get(String key, T defaultValue){
		return LangUtil.turn(getProperty(key, ""), defaultValue);
	}
	public static <T> T get(String key){
		return get(key, null);
	}

	
	
	public static void saveProperty(String key, String value){
		proper.setProperty(key, value);
		try{
	        FileOutputStream oFile = new FileOutputStream(settingFileName, false);
	        proper.store(oFile, "Change at " + TimeUtil.getTimeYmdHmss());
	        oFile.close();
		}catch(Exception e){
			ExceptionUtil.on(e);
		}
	}
	public static String showString(){
		String res = "";
	    Iterator<String> it=proper.stringPropertyNames().iterator();
        while(it.hasNext()){
            String key=it.next();
            res += key + "=" + proper.getProperty(key);
        }
        return res;
	}
	
	
}
