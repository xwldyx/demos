package demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.entity.Account;
import demo.entity.Role;
import demo.repository.AccountRepository;
import demo.repository.RoleRepository;
import demo.security.JwtTokenUtils;

@RestController
public class AccountController {

	@Autowired  
    private AccountRepository accountRepository;
	@Autowired  
    private RoleRepository roleRepository; 
	
	@RequestMapping(value = "/testb/**")
	public String testb(String model) {
		return "this is b " + model;
	}

	@RequestMapping(value = "/login")
	public String login(HttpServletResponse response, String userName, String password) {
		Account acc = accountRepository.findByUserName(userName).orElseThrow(() -> new RuntimeException("找不到用户："+userName));
		if (password.equals(acc.getPassword())) {
			List<Role> roles = roleRepository.findByAccountId(acc.getId());
			List<String> authorities = roles.stream().map(role -> {
				return role.getCode();
			}).collect(Collectors.toList());
			String auth = String.join(",", authorities);
			return JwtTokenUtils.setToken(response, userName, auth);
		}else {
			throw new RuntimeException("密码错误");
		}
	}
	
	@GetMapping(value = "/verifyToken")
	public boolean verifyToken(String token) {
		return JwtTokenUtils.verifyToken(token);
	}
}
