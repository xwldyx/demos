//package demo.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.provisioning.InMemoryUserDetailsManager;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfiguration  {
//
//	@Bean
//	@Override
//	protected UserDetailsService userDetailsService(){
//		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
//		manager.createUser(User.withUsername("admin").password("admin").authorities("ADMIN").build());
//		manager.createUser(User.withUsername("user").password("123456").authorities("USER").build());
//		return manager;
//	}
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.requestMatchers().anyRequest()
//			.and()
//			.authorizeRequests()
//			.antMatchers("/oauth/*").permitAll();
//	}
//
//	@Bean
//	@Override
//	public AuthenticationManager authenticationManagerBean() throws Exception {
//		return super.authenticationManagerBean();
//	}
//}
