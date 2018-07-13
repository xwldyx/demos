package demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.dao.TCitywebPropertyDao;
import demo.model.TCitywebProperty;

@RestController
@RequestMapping(value = "/public")
public class PublicController {
	
	@Autowired
	private TCitywebPropertyDao tCitywebPropertyMapper;
	
	@RequestMapping(value = "/testme")
	public List<TCitywebProperty> test(Model model) {
		List<TCitywebProperty> list = tCitywebPropertyMapper.findByPropKey("channelOrder");
		return list;
	}
	
	@RequestMapping(value = "/test")
	public String test() {
		return "this is public";
	}
	
}
