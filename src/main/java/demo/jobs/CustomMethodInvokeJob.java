package demo.jobs;

import java.lang.reflect.Method;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 没用，无法分布式，数据库存储时无法序列化复杂的jobDataMap对象，spring本身的MethodInvokeJob也是一样的问题
 * 这里targetObject如果支持序列化应该就可以了，但是不太想把对象序列化到数据库
 * @author lwx
 *
 */
public class CustomMethodInvokeJob implements Job {

	static Logger logger = LoggerFactory.getLogger(CustomMethodInvokeJob.class);

	protected void doit() {
		logger.info("执行doit，执行时间：{}",new Date());
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		Object targetObject = jobDataMap.get("targetObject");
		String targetMethod = (String) jobDataMap.get("targetMethod");
		Object[] arguments = (Object[]) jobDataMap.get("arguments");
		Method method = getMethod(targetObject, targetMethod, arguments);
//		method.setAccessible(true);
		try {
			method.invoke(targetObject, arguments);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	private Method getMethod(Object targetObject, String targetMethod, Object[] arguments) {
		Class<?>[] argTypes = new Class<?>[arguments.length];
		for (int i = 0; i < arguments.length; ++i) {
			argTypes[i] = (arguments[i] != null ? arguments[i].getClass() : Object.class);
		}

		try {
			return targetObject.getClass().getMethod(targetMethod, argTypes);
		}catch (NoSuchMethodException ex) {
			logger.error(ex.getMessage());
		}
		return null;
	}
}
