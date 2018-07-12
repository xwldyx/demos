//package demo.security;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import demo.entity.Account;
//import demo.entity.Role;
//import demo.repository.AccountRepository;
//import demo.repository.RoleRepository;
//
//class CustomAuthenticationProvider implements AuthenticationProvider {
//
//	@Autowired  
//    private AccountRepository accountRepository;
//	@Autowired  
//    private RoleRepository roleRepository; 
//	
//	@Override
//	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//		String name = authentication.getName();
//		String password = authentication.getCredentials().toString();
//
//		Account acc = accountRepository.findByUserName(name).orElseThrow(() -> new UsernameNotFoundException("找不到用户："+name));
//		if (password.equals(acc.getPassword())) {
//			List<Role> roles = roleRepository.findByAccountId(acc.getId());
//			List<GrantedAuthority> authorities = roles.stream().map(role -> {
//				return new SimpleGrantedAuthority(role.getCode());
//			}).collect(Collectors.toList());
//
//			return new UsernamePasswordAuthenticationToken(name, password, authorities);
//		}else {
//			throw new BadCredentialsException("密码错误");
//		}
//	}
//
//	@Override
//	public boolean supports(Class<?> authentication) {
//		return authentication.equals(UsernamePasswordAuthenticationToken.class);
//	}
//}