package com.saofang.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saofang.demo.dao.TCitywebPropertyDao;
import com.saofang.demo.model.TCitywebProperty;

@RestController
public class TestController {
	
	@Autowired
	private TCitywebPropertyDao tCitywebPropertyMapper;
	
	@RequestMapping(value = "/testme")
	public List<TCitywebProperty> test(Model model) {
		List<TCitywebProperty> list = tCitywebPropertyMapper.findByPropKey("channelOrder");
		List<Integer> ids = new ArrayList<>();
		ids.add(55);
		ids.add(66);
		System.out.println();
		tCitywebPropertyMapper.deleteByIdIn(ids);
		return list;
	}
	
	@RequestMapping(value = "/testb/**")
	public String testb(String model) {
		return "this is b " + model;
	}
	
	public void auth() {
		
	}
}
