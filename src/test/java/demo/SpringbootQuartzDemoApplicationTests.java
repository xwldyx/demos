package demo;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.SchedulerException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootQuartzDemoApplicationTests {

	@Test
	public void contextLoads() throws SchedulerException, IOException {
		System.in.read();
	}

}
