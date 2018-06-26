package demo.security;

import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenUtils {
	static final long EXPIRATIONTIME = 432_000_000;     // 5天
	static final String SECRET = "P@ssw02d";            // JWT密码
	static final String TOKEN_PREFIX = "Bearer";        // Token前缀
	static final String HEADER_STRING = "Authorization";// 存放Token的Header Key

	public static String setToken(HttpServletResponse response, String userName, String authorities) {
		String token = Jwts.builder()
				.claim("authorities", authorities)
				.setAudience(userName)
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.signWith(SignatureAlgorithm.HS512, SECRET)
				.compact();
		return token;
		/*// 将 JWT 写入 body
		try {
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getOutputStream().println(JWT);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	/**
	 * 获取用户名
	 * @param request
	 * @return
	 */
	public static String getUser(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
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
	public static String[] getAuthorities(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
		if (token != null) {
			Claims claims = getClaimsFromToken(token);
			return claims.get("authorities").toString().split(",");
		}else
			return null;
	}

	public static boolean verifyToken(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
		if (token != null) {

			return !isTokenExpired(token);
		}else
			return false;
	}
	
	public static boolean verifyToken(String token) {
		if (token != null) {

			return !isTokenExpired(token);
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