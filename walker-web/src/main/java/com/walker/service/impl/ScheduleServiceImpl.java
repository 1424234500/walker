package com.walker.service.impl;

import com.walker.core.scheduler.Task;
import com.walker.service.ScheduleService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * quartz实现
 *
 */
@Service("scheduleService")
class ScheduleServiceImpl implements ScheduleService {
	private Logger log = LoggerFactory.getLogger(getClass());


	@Autowired
	SchedulerFactoryBean schedulerFactoryBean;

	private Scheduler getScheduler() {
		return schedulerFactoryBean.getScheduler();
	} 

	@Override
	public void start() throws Exception {
		log.info("start");
		if(!getScheduler().isStarted())
			getScheduler().start();
	}
	@Override
	public void pause() throws Exception {
		log.info("pause");
		getScheduler().pauseAll();
	}
	@Override
	public void shutdown() throws Exception {
		log.info("shutdown");
		if(!getScheduler().isShutdown())
			getScheduler().shutdown();
	}

	@Override
	public Task add(Task task) throws Exception {
		log.info("add " + task.toString());
		Scheduler scheduler = getScheduler();
		JobDetail jobDetail = task.getJobDetail();//makeJobDetail(task);

		scheduler.addJob(jobDetail, false);	//添加方式2

		List<Trigger> triggers = task.getTriggers();//makeTrigger(task);
		// 将任务及其触发器放入调度器
		for(Trigger trigger : triggers) {
//			scheduler.scheduleJob(jobDetail, trigger);//添加方式1 只能单触发器
			scheduler.scheduleJob(trigger);//添加方式2 trigger绑定job
		}
		start();
		return task;
	}

	@Override
	public Task run(Task task) throws Exception {
		log.info("run " + task.toString());

		Scheduler scheduler = getScheduler();
		JobDetail jobDetailNew = task.getJobDetail();
		JobKey jobKey = jobDetailNew.getKey();

		JobDetail job = jobDetailNew;
		if(scheduler.checkExists(jobKey)) {
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			job = jobDetail;
		}
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
		triggerBuilder.withSchedule(SimpleScheduleBuilder.repeatSecondlyForTotalCount(1, 1));
		triggerBuilder.withDescription("run now once");
		triggerBuilder.forJob(job);
		scheduler.scheduleJob(triggerBuilder.build());

		return task;
	}

	@Override
	public Task remove(Task task) throws Exception {
		log.info("remove " + task.toString());
		JobDetail job = task.getJobDetail();
		if(getScheduler().checkExists(job.getKey())) {
			getScheduler().deleteJob(job.getKey());
		}else{
			log.warn("the job not exists " + task);
		}
		return task;
	}
	/**
	 *
	 * 以className删除重建job 继承触发器 备注
	 */
	@Override
	public Task save(Task task) throws Exception {
		Scheduler scheduler = getScheduler();

		log.info("save " + task.toString());
		JobDetail jobDetailNew = task.getJobDetail();
		JobKey jobKey = jobDetailNew.getKey();
		if(scheduler.checkExists(jobKey)){
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			if(task.getAbout() == null || task.getAbout().length() == 0){
				task.setAbout(jobDetail.getDescription());
			}
			if(task.getPattern().size() == 0){
				List<? extends Trigger> triggerList = scheduler.getTriggersOfJob(jobKey);
				for(Trigger trigger : triggerList){
					CronTrigger cronTrigger = (CronTrigger) trigger;
					String key = cronTrigger.getCronExpression();
					task.addCron(key);
					log.info("extends old trigger " + key);
				}
			}
			scheduler.deleteJob(jobKey);
		}
		return add(task);
	}

	/**
	 * 添加 删除 触发器
	 */
	@Override
	public Task saveTrigger(Task task, List<String> cronOn, List<String> cronOff) throws Exception {
		log.info("saveTrigger " + task + " on:" + cronOff + " off:" + cronOn);
		Scheduler scheduler = getScheduler();
		JobDetail jobDetailNew = task.getJobDetail();
//		List<Trigger> triggerList = task.getTriggers();
		JobKey jobKey = jobDetailNew.getKey();
		if (scheduler.checkExists(jobKey)) {
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			task.setAbout(jobDetail.getDescription());
			List<? extends Trigger> triggerList = scheduler.getTriggersOfJob(jobKey);
			for(Trigger trigger : triggerList){
				if(trigger instanceof  CronTrigger) {	//多种触发器问题
					CronTrigger cronTrigger = (CronTrigger) trigger;
					String key = cronTrigger.getCronExpression();
					if (cronOff.contains(key)) {
						log.info("drop old trigger " + key);
						scheduler.unscheduleJob(cronTrigger.getKey());
					}
				}else{
					log.info("drop old trigger " + trigger);
					scheduler.unscheduleJob(trigger.getKey());
				}

			}
			triggerList = Task.getTriggers(cronOn, jobDetail);
			for(Trigger trigger : triggerList){
				log.info("add trigger " + trigger);
				scheduler.scheduleJob(trigger);
			}

		} else {
			throw new RuntimeException("the job " + task + " is not exists ");
		}
		return task;
	}


}
