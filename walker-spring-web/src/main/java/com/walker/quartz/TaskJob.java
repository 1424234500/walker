package com.walker.quartz;

import com.walker.core.mode.sys.LogModel;
import com.walker.core.util.Tools;
import com.walker.service.Config;
import com.walker.service.LogService;
import com.walker.spring.component.SpringContextUtil;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 *  extends org.quartz.InterruptableJob	?
 */
public abstract class TaskJob extends QuartzJobBean {
	private final Logger log = LoggerFactory.getLogger(getClass());


	LogService logService = SpringContextUtil.getBean("logService");
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException{
		JobDetail jobDetail = context.getJobDetail();
		Trigger trigger = context.getTrigger();
		String triggerInfo = "";
		if(trigger instanceof CronTrigger) {
			CronTrigger cronTrigger = (CronTrigger) trigger;
			triggerInfo = cronTrigger.getCronExpression() + " " + cronTrigger.getDescription();
		}else{
			triggerInfo = trigger.toString();
		}
		String jobName = jobDetail.getKey().getName();
		String className = jobDetail.getJobClass().getName();
		String status = "2";//状态0失败2执行中1成功
		long startTime = System.currentTimeMillis();
		log.info("Scheduler quartz " + className + " " + triggerInfo);
		log.info("Desc:" + jobDetail.getDescription());
		//任务名 类名 触发器名
		LogModel logModel = LogModel.getDefaultModel()
				.setCATE(Config.getCateJob())
				.setUSER(Config.getSystemUser())
				.setWAY(jobName)
				.setURL(className)
				.setARGS(triggerInfo)
				.setIS_OK(status)
				.setABOUT("任务调度日志")
				.setRES(null);

		logModel = logService.saveLogModelNoTime(logModel);
		try {
			String res = this.make();

			status = "1";
			logModel.setIS_EXCEPTION(Config.FALSE)
					.setRES(res)
					;
		}catch (Exception e){
			status = "0";
			logModel.setEXCEPTION(Tools.toString(e));
			throw e;
		}finally {
			logModel.setIS_OK(status);
			if(logModel.getIS_EXCEPTION().equalsIgnoreCase(Config.TRUE)){
				log.error(logModel.toString());
			}else{
				log.info(logModel.toString());
			}
			logModel = logService.saveLogModel(logModel);
		}

	}

	public abstract String make();

}
