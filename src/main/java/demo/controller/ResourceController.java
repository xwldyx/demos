package demo.controller;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.dao.TCitywebPropertyDao;
import demo.model.TCitywebProperty;

@RestController
@RequestMapping(value = "/resource")
public class ResourceController {
	
	@Autowired
	private TCitywebPropertyDao tCitywebPropertyMapper;
	
	@RequestMapping(value = "/testme")
	public List<TCitywebProperty> test(Model model) {
		List<TCitywebProperty> list = tCitywebPropertyMapper.findByPropKey("channelOrder2");
		return list;
	}
	
	@RequestMapping(value = "/test")
	public String test() {
		return "this is oauth2";
	}
}
