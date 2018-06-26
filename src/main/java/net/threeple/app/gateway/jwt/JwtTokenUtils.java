package net.threeple.app.gateway.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);
	
	static final long EXPIRATIONTIME = 5_000;     // 5天
	static final String SECRET = "P@ssw02d";            // JWT密码
	static final String TOKEN_PREFIX = "Bearer";        // Token前缀
	static final String HEADER_STRING = "Authorization";// 存放Token的Header Key

	public static String setToken(String userName, String authorities) {
		String token = Jwts.builder()
				.claim("authorities", authorities)
				.setAudience(userName)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.signWith(SignatureAlgorithm.HS512, SECRET)
				.compact();
		return token;
	}

	/**
	 * 获取用户名
	 * @param request
	 * @return
	 */
	public static String getUser(String token) {
		if (token != null) {
			Claims claims = getClaimsFromToken(token);
			return claims.getAudience();
		}else
			return null;
	}

	/**
	 * 获取权限列表roles
	 * @param request
	 * @return
	 */
	public static String[] getAuthorities(String token) {
		if (token != null) {
			Claims claims = getClaimsFromToken(token);
			return claims.get("authorities").toString().split(",");
		}else
			return null;
	}

	public static boolean verifyToken(String token) {
		if (token != null) {
			try {
				return !isTokenExpired(token);
			}catch(Exception e) {
				logger.error(e.getMessage());
				return false;
			}
		}else
			return false;
	}

	private static Boolean isTokenExpired(String token) {
		Claims claims = getClaimsFromToken(token);
		final Date expiration = claims.getExpiration();
		return expiration.before(new Date());
	}

	private static Claims getClaimsFromToken(String token) {
		return Jwts.parser()
				.setSigningKey(SECRET)
				.parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
				.getBody();
	}
}