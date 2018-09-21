/*package demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduler")
public class QuartzController {

	@Autowired
	private SchedulerManager schedulerManager;
	
	@GetMapping("/add")
	public void addScheduler(String targetClass, String targetMethod) {
//		schedulerManager.addScheduler(targetClass, targetMethod, new Object[0]);
	}
	
	@GetMapping("/remove")
	public void deleteScheduler(String name, String group) {
		schedulerManager.deleteScheduler(name, group);
	}
	
	@GetMapping("/update")
	public void updateScheduler() {
		
	}
	
	@GetMapping("/list")
	public void listScheduler() {
		
	}
}
*/