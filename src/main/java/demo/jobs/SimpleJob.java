package demo.jobs;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 同一个job可能并发执行，如前一个没执行完又到了触发时间，会并发执行同一个job
 * @author lwx
 *
 */
public class SimpleJob implements Job {

	static Logger logger = LoggerFactory.getLogger(SimpleJob.class);


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("执行doit，执行时间：{}",new Date());
	}
	
}
