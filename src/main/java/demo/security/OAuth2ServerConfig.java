package demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class OAuth2ServerConfig extends WebSecurityConfigurerAdapter {

	private static final String DEMO_RESOURCE_ID = "resource";

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
    PasswordEncoder passwordEncoder(){
//		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return NoOpPasswordEncoder.getInstance();
    }
	
	@Bean
	@Override
	protected UserDetailsService userDetailsService(){
		InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
		//注意使用authorities时一定要加前缀ROLE_，使用roles不要加会自动加，两者是等效的，但是万一搞混了authorities没加前缀，在权限比较时就会不准
		manager.createUser(User.withUsername("admin").password("123456").authorities("ROLE_ADMIN").build());
		manager.createUser(User.withUsername("user").password("123456").roles("USER").build());
		return manager;
	}
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
		.and()
		.requestMatchers().anyRequest();
	}
	
	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(DEMO_RESOURCE_ID).stateless(true);
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			http
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
			.and()
			.requestMatchers().anyRequest()
			.and()
			.authorizeRequests()
			//注意使用authorities时一定要加前缀ROLE_，使用roles不要加会自动加，两者是等效的，但是万一搞混了authorities没加前缀，在权限比较时就会不准
			//比如说这里的hasRole('USER')它会帮你加前缀，而上面用authorities没加前缀，字符串比较就不对了
			.antMatchers("/resource/testme").access("#oauth2.hasScope('testme') and hasRole('USER')")
			.antMatchers("/resource/**").authenticated();
		}
	}


	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		AuthenticationManager authenticationManager;
		@Autowired
		RedisConnectionFactory redisConnectionFactory;

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			//配置两个客户端,一个用于password认证一个用于client认证
			clients.inMemory().withClient("client_credentials")
			.resourceIds(DEMO_RESOURCE_ID)
			.authorizedGrantTypes("client_credentials", "refresh_token")
			.scopes("testme")
			.authorities("ROLE_USER")
			.secret("123456")
			.and().withClient("password_grant")
			.resourceIds(DEMO_RESOURCE_ID)
			.authorizedGrantTypes("password", "refresh_token")
			.scopes("testme")
			.authorities("ROLE_USER")
			.secret("123456");
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints/*.tokenStore(new RedisTokenStore(redisConnectionFactory))*/
			.authenticationManager(authenticationManager);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			//允许表单认证
			oauthServer.allowFormAuthenticationForClients();
		}

	}

}