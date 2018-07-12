package com.saofang.demo;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.saofang.demo.dao.TCitywebPropertyDao;
import com.saofang.demo.model.TCitywebProperty;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
	@Autowired
	private TCitywebPropertyDao tCitywebPropertyMapper;

	@Test
	public void contextLoads() {
		List<TCitywebProperty> list = tCitywebPropertyMapper.findByPropKey("channelOrder");
		list.forEach(obj -> System.out.println(obj.getPropValue()));
	}

}
