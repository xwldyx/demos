package demo;

import org.quartz.SchedulerException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringbootQuartzDemoApplication {

	public static void main(String[] args) throws SchedulerException {
		ConfigurableApplicationContext context = SpringApplication.run(SpringbootQuartzDemoApplication.class, args);
		SchedulerManager schedulerManager = context.getBean(SchedulerManager.class);
		schedulerManager.loadScheduler();
	}
}
