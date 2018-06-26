package net.threeple.app.gateway.filter;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import io.netty.handler.codec.http.HttpMethod;
import net.threeple.app.gateway.jwt.JwtTokenUtils;
import reactor.ipc.netty.http.client.HttpClient;

public class VerifyJwtTokenGatewayFilterFactory extends AbstractGatewayFilterFactory<VerifyJwtTokenGatewayFilterFactory.Config> {
	private static final Logger logger = LoggerFactory.getLogger(VerifyJwtTokenGatewayFilterFactory.class);
	private final HttpClient httpClient;
	
	private boolean remoteEnable = false;
	

	public VerifyJwtTokenGatewayFilterFactory(HttpClient httpClient) {
		super(Config.class);
		this.httpClient = httpClient;
	}

	@Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList("authHeader", "url");
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
			if(route.getId().equals("account")) {
				return chain.filter(exchange);
			}
			String token = exchange.getRequest().getHeaders().getFirst(config.getAuthHeader());
			if(!StringUtils.isEmpty(token) && verify(token, config.getUrl())) {
					logger.info("Jwt verify success");
					return chain.filter(exchange);
			}else {
				logger.info("Jwt verify fail");
				final ServerHttpResponse response = exchange.getResponse();
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				return response.setComplete();
			}
		};
	}
	
	private boolean verify(String token, String url) {
		if(remoteEnable) {
			return remoteVerify(token, url);
		}else {
			return localVerify(token);
		}
	}
	
	private boolean remoteVerify(String token, String url) {
		//TODO httpclient 如何同步地发起请求；  据说netty nio接受请求过程中是有验证过程的，是否能嵌入到那里
		this.httpClient.request(HttpMethod.GET, url, null)
			.doOnSuccess(r -> {
				System.out.println("sdf"+r.receive().toString());
				r.receive().asString().toString();
			}).block();
		return false;
	}
	
	private boolean localVerify(String token) {
		//TODO 权限验证  String[] authorities = JwtTokenUtils.getAuthorities(req);
		return JwtTokenUtils.verifyToken(token);
	}

	public static class Config {
		private String authHeader;
		String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getAuthHeader() {
			return authHeader;
		}

		public void setAuthHeader(String authHeader) {
			this.authHeader = authHeader;
		}
	}

}
