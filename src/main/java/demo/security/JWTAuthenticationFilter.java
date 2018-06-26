package demo.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.GenericFilterBean;

@WebFilter(filterName = "jwtAuthenticationFilter", urlPatterns = "/*")
class JWTAuthenticationFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
//		if(httpRequest.getRequestURI().matches("^/login(/\\w*)*$")) {
//			chain.doFilter(request, response);
//		}
//		if(JwtTokenUtils.verifyToken(httpRequest)) {
////			String[] authorities = JwtTokenUtils.getAuthorities(req);
//			chain.doFilter(request, response);
//		}
//		httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    
	}
}