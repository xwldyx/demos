package net.threeple.app.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import net.threeple.app.gateway.filter.VerifyJwtTokenGatewayFilterFactory;
import reactor.ipc.netty.http.client.HttpClient;


@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
	
	@Bean
	public VerifyJwtTokenGatewayFilterFactory verifyJwtTokenGatewayFilterFactory(HttpClient httpClient) {
		return new VerifyJwtTokenGatewayFilterFactory(httpClient);
	}

}
