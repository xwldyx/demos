package demo;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 最佳方式应该是定时任务本身一个集群，然后有一个管理节点对外开放管理接口
 * @author lwx
 *
 */
@Component
public class SchedulerManager {
	private static Logger logger = LoggerFactory.getLogger(SchedulerManager.class);
	
	@Autowired
	private Scheduler scheduler;
	
	public void loadScheduler() {
		try {
			//TODO 从数据库读取配置，对未有job执行添加，对已有略过，对已删除（删掉了或者标志位）执行删除
			String cronExpression = "0/20 * * * * ?";
			String name = "abc";
			String group = "db";
			String targetClass = "demo.jobs.SimpleJob";
			Object[] arguments= new Object[0];
			addScheduler(name, group, cronExpression, targetClass, arguments);
			updateScheduler(name, group, cronExpression);
			scheduler.startDelayed(10);
		} catch (SchedulerException e) {
			logger.error(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void addScheduler(String name, String group, String cronExpression, String targetClass, Object... arguments) {
		Class<? extends Job> clazz = null;
		try {
			clazz = (Class<? extends Job>) Class.forName(targetClass);
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFound for {}", targetClass);
		}

		//创建任务
		JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(name, group).build();
		jobDetail.getJobDataMap().put("arguments", arguments);
        
		//创建任务触发器
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
		Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(name, group).withSchedule(scheduleBuilder).build();
		//将触发器与任务绑定到调度器内
		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			logger.error("调度器增加job异常：{}", e.getMessage());
		}
		
	}
	
	public void deleteScheduler(String name, String group) {
		JobKey jobKey = JobKey.jobKey(name, group);
		try {
			scheduler.deleteJob(jobKey);
		} catch (SchedulerException e) {
			logger.error("调度器移除job失败：{}", e.getMessage());
		}
	}

	public void updateScheduler(String name, String group, String cronExpression) {
		JobKey jobKey = JobKey.jobKey(name, group);
		TriggerKey triggerKey = TriggerKey.triggerKey(name, group);
		try {
			scheduler.pauseJob(jobKey);
			JobDetail jobDetail = scheduler.getJobDetail(jobKey);
			//创建任务触发器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
			Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(name, group).withSchedule(scheduleBuilder).build();
			scheduler.rescheduleJob(triggerKey, trigger);
		} catch (SchedulerException e) {
			logger.error("调度器移除job失败：{}", e.getMessage());
		}
	}
}
