package demo.jobs;

import java.util.Date;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 同一个job不能同时执行
 * @author lwx
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StatefulSimpleJob implements Job {

	static Logger logger = LoggerFactory.getLogger(StatefulSimpleJob.class);


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("执行Stateful，执行时间：{}",new Date());
	}
	
}
