package com.walker.core.scheduler;

import com.walker.util.ClassUtil;
import org.quartz.*;

import java.util.*;

/**
 * 用于任务调度的任务
 * <p>
 * 定时执行某个函数
 */
public class Task {
	/**
	 * id 任务id标识
	 */
	String id = "";

	/**
	 * com.task.TaskTest
	 */
	String className = "";

	/**
	 * 这是个测试任务
	 */
	String about = "";

	/**
	 * 任务添加 使用job triiger
	 */
	JobDetail jobDetail;
	List<Trigger> trigger;

	/**
	 * 多触发器
	 * CRON表达式    含义
	 */
	Set<String> pattern;

	/**
	 * new Task("util.scheduler.job.JobTest","quartz scheduler tools out");
	 */
	public Task() {
		pattern = new HashSet<>();
		trigger = new ArrayList<>();
	}

	public Task(String id) {
		this();
		this.id = id;
	}

	/**
	 * 构造一个任务
	 * new Task("util.scheduler.job.JobTest","quartz scheduler tools out", "");
	 * Cron表达式的格式：秒 分 时 日 月 周 年(可选)。
	 * 字段名              允许的值                    允许的特殊字符
	 * 秒                     0-59                           , - * /
	 * 分                     0-59                           , - * /
	 * 小时                  0-23                           , - * /
	 * 日                     1-31                            , - * ? / L W C
	 * 月                     1-12 or JAN-DEC        , - * /
	 * 周几                  1-7 or SUN-SAT         , - * ? / L C #
	 * 年(可选字段)     empty                         1970-2099 , - * /
	 * <p>
	 * <p>
	 * * ：代表所有可能的值。因此，“*”在Month中表示每个月，在Day-of-Month中表示每天，在Hours表示每小时
	 * <p>
	 * - ：表示指定范围。
	 * <p>
	 * , ：表示列出枚举值。例如：在Minutes子表达式中，“5,20”表示在5分钟和20分钟触发。
	 * <p>
	 * / ：被用于指定增量。例如：在Minutes子表达式中，“0/15”表示从0分钟开始，每15分钟执行一次。"3/20"表示从第三分钟开始，每20分钟执行一次。和"3,23,43"（表示第3，23，43分钟触发）的含义一样。
	 * <p>
	 * ? ：用在Day-of-Month和Day-of-Week中，指“没有具体的值”。当两个子表达式其中一个被指定了值以后，为了避免冲突，需要将另外一个的值设为“?”。
	 * <p>
	 * 例如：想在每月20日触发调度，不管20号是星期几，只能用如下写法：0 0 0 20 * ?，其中最后以为只能用“?”，而不能用“*”。
	 * <p>
	 * 0 * * * * ? 每1分钟触发一次
	 * 0 0 * * * ? 每1小时触发一次
	 * 0 0 10 * * ? 10点触发一次
	 * 0/11 * * * * ? 每隔11s
	 * 0 0/5 * * * ? 每隔5分钟
	 */
	public Task(String id, String className, String about, String... crons) {
		this();
		this.id = id;
		this.className = className;
		this.about = about;
		if (crons.length > 0) {
			pattern.addAll(Arrays.asList(crons));
		}
	}

	public static List<Trigger> getTriggers(List<String> trr, JobDetail jobDetail) {
		List<Trigger> trigger = new ArrayList<>();
		for (String cron : new HashSet<String>(trr)) {
			TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
			triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
			triggerBuilder.withDescription(makeInfo(cron));
			triggerBuilder.forJob(jobDetail);
			trigger.add(triggerBuilder.build());
		}
		return trigger;
	}

	public static String makeInfo(String cron) {
//		秒                     0-59                           , - * /
//	 * 分                     0-59                           , - * /
//	 * 小时                  0-23                           , - * /
//	 * 日                     1-31                            , - * ? / L W C
//	 * 月                     1-12 or JAN-DEC        , - * /
//	 * 周几                  1-7 or SUN-SAT         , - * ? / L C #
//	 * 年(可选字段)     empty                         1970-2099 , - * /
		StringBuilder sb = new StringBuilder();
		String[] ds = {"秒", "分", "时", "日!?", "月", "周?", "(年)"};
		String[] cs = cron.split(" +");
		for (int i = 0; i < cs.length && i < ds.length; i++) {
			sb.append(cs[i] + ds[i]).append(" ");
		}
		return sb.toString();
	}

	/**
	 * 以反射 类名 函数名 和参数名作为统一 id
	 */
	@Override
	public String toString() {
		return "Task:" + className + " Trigger:" + pattern + " [" + about + "]";
	}

	public void addCron(String str) {
		this.pattern.add(str);
	}

	public Boolean removeCron(String str) {
		return this.pattern.remove(str);
	}

	@SuppressWarnings("unchecked")
	public JobDetail getJobDetail() {
		Class<? extends Job> clz = null;
		if (className != null && className.length() > 0) {
			clz = (Class<? extends Job>) ClassUtil.loadClass(className);
		}
		this.jobDetail = JobBuilder
				.newJob(clz)
				.withIdentity(this.id)    //主键
				.withDescription(this.about)
				.storeDurably()    //持久化
				.build();

		return this.jobDetail;
	}

	public Task setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
		return this;
	}

	public List<Trigger> getTriggers() {
		this.trigger.clear();
		JobDetail jobDetail = this.getJobDetail();
		Set<String> trr = this.pattern;
		for (String cron : trr) {
			TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
			triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
			triggerBuilder.withDescription(makeInfo(cron));
			triggerBuilder.forJob(jobDetail);
			this.trigger.add(triggerBuilder.build());
		}
		return this.trigger;
	}

	public String getId() {
		return id;
	}

	public Task setId(String id) {
		this.id = id;
		return this;
	}

	public String getClassName() {
		return className;
	}

	public Task setClassName(String className) {
		this.className = className;
		return this;
	}

	public String getAbout() {
		return about;
	}

	public Task setAbout(String about) {
		this.about = about;
		return this;
	}

	public Set<String> getPattern() {
		return pattern;
	}

	public Task setPattern(Set<String> pattern) {
		this.pattern = pattern;
		return this;
	}
}
	
