package com.walker.common.setting;

import com.walker.common.util.Bean;
import com.walker.common.util.MapListUtil;
import com.walker.common.util.TimeUtil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * 配置文件properties读写工具
 * xxx.properties <-> Bean
 * xxx.properties <-> Key-Value
 * 
 */
public class SettingUtil {
	
	public static Bean getSetting(String filename){
		Properties proper = new Properties();     
        try{
            InputStream in = new BufferedInputStream (new FileInputStream(filename));
            proper.load(in);
            in.close(); 
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Bean bean = new Bean(proper); 
        Bean res = new Bean();
        
        for(Object item : bean.keySet()){
        	MapListUtil.putMapUrl(res, item.toString(), bean.get(item));
        }
        
        return res;
	}
	public static void saveSetting(String filename, Bean bean){
		Properties proper = new Properties();     
		proper.putAll(bean);
		try{
	        FileOutputStream oFile = new FileOutputStream(filename, false);//true表示追加打开
	        proper.store(oFile, "Change at " + TimeUtil.getTimeYmdHms());
	        oFile.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	} 
	
}
