package com.walker.config;

import com.walker.common.util.FileUtil;
import com.walker.common.util.TimeUtil;
import com.walker.common.util.Watch;
import com.walker.core.aop.Fun;
import com.walker.mode.FileIndex;
import com.walker.service.Config;
import com.walker.service.FileIndexService;
import com.walker.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 定时器任务
 */
@Component
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableAsync        // 2.开启多线程
@EnableScheduling   // 3.开启定时任务
public class ScheduleConfig {
	private Logger log = LoggerFactory.getLogger(getClass());

	public ScheduleConfig(){
		log.info(com.walker.service.Config.getPre() + "ScheduleConfig");
	}

	@Autowired
	private DataSource dataSource;

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setSchedulerName("Cluster_Scheduler");
		factory.setDataSource(dataSource);
		factory.setApplicationContextSchedulerContextKey("applicationContext");
		factory.setQuartzProperties(quartzProperties());
		return factory;
	}

	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));

		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}



	static private long count = 0;
	@Autowired
	LogService logService;
	@Autowired
	FileIndexService fileIndexService;

	@Scheduled(cron = "59 59 23 ? * *") //每天
	public void everyDay() {
	    log.info("[eachDay 23:59][每天任务]");
	}

	@Scheduled(cron = "0 0/59 * * * ?") //每小时
	public void everyHour() {
	    log.info("[eachHour 59m][每小时任务]");
	    
	    log.info("扫描同步上传文件");
		Watch w = new Watch("scan");
	    //刷新上传文件集合的 文件数据到 内存数据库？ 文件管理系统 展示文件 介绍（图片），
		String root = com.walker.service.Config.getUploadDir();
		List<FileIndex> listSave = new ArrayList<>();
		List<File> list = FileUtil.showDir(root, new Fun<File>(){
			@Override
			public Boolean make(File obj) {
				if(obj.isFile()){
					String key = "";
					try {
						key = "" + FileUtil.checksumMd5(obj);
					} catch (IOException e) {
						e.printStackTrace();
					}
					String name = obj.getName();
					String ext = FileUtil.getFileType(name);
					FileIndex fileIndex = fileIndexService.get(new FileIndex().setID(key));
					if (fileIndex == null) {
						fileIndex = new FileIndex();
						fileIndex
								.setID(key)
								.setINFO("scan")
								.setOWNER("001")
								.setS_FLAG("1")
								.setS_ATIME(TimeUtil.getTimeYmdHms())
						;
					}
					fileIndex
							.setPATH(obj.getAbsolutePath())
							.setNAME(name)
							.setEXT(ext)
							.setS_MTIME(TimeUtil.getTimeYmdHms())
							.setLENGTH(obj.length() + "")
					;

					listSave.add(fileIndex);
					if(listSave.size() > Config.getDbsize()){
						fileIndexService.saveAll(listSave);
						listSave.clear();
					}
 				}
				if(obj.isDirectory() && obj.getAbsolutePath().endsWith("_temp")){
					return false;
				}
				return true;
			}
		});
		if(listSave.size() > 0){
			fileIndexService.saveAll(listSave);
		}

		w.cost("扫描完毕");
		w.put("扫描文件数", list.size());
		log.info(w.toPrettyString());
	}

	/**
	 * 允许多线程 前后执行周期独立
	 */
	@Async
	@Scheduled(cron = "0/60 * * * * ?") //每分钟
	public void everyMinute() {
	    log.info("[eachMinute 0/60 * * * * ?][每分钟任务]");
		log.info("Redis操作记录持久化"); 
	    //刷新redis到oracle provider自动调用
//	    logService.saveStatis();
//	    fileService.scan();

	}

	@Scheduled(cron = "0/10 * * * * ?") //每10s
	public void everySec10() {
		log.info("[eachSec10 0/10 * * * * ?][每10s任务]" + count ++);
	}


	
	/*
	cronExpression的配置说明，具体使用以及参数请百度google
	字段   允许值   允许的特殊字符
	秒    0-59    , - * /
	分    0-59    , - * /
	小时    0-23    , - * /
	日期    1-31    , - * ? / L<last> W C
	月份    1-12 或者 JAN-DEC    , - * /
	星期    1-7 或者 SUN-SAT    , - * ? / L C #
	年（可选）    留空, 1970-2099    , - * / 
	- 区间  
	* 通配符  
	? 你不想设置那个字段
	 
	CRON表达式    含义 
	"0 0 12 * * ?"    每天中午十二点触发 
	"0 15 10 ? * *"    每天早上10：15触发 
	"0 15 10 * * ?"    每天早上10：15触发 
	"0 15 10 * * ? <*>"    每天早上10：15触发 
	"0 15 10 * * ? 2005"    2005年的每天早上10：15触发 
	"0 *  14 * * ?"    每天从下午2点开始到2点59分每分钟一次触发 
	"0 0/5 14 * * ?"    每天从下午2点开始到2：55分结束每5分钟一次触发 
	"0 0/5 14,18 * * ?"    每天的下午2点至2：55和6点至6点55分两个时间段内每5分钟一次触发 
	"0 0-5 14 * * ?"    每天14:00至14:05每分钟一次触发 
	"0 10,44 14 ? 3 WED"    三月的每周三的14：10和14：44触发 
	"0 15 10 ? * MON-FRI"    每个周一、周二、周三、周四、周五的10：15触发 
	*
	*
	*/
	
	
}
