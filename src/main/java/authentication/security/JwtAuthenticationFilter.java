package authentication.security;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bebracore.cabinet.model.User;
import com.bebracore.cabinet.service.UserService;

import authentication.security.error.jwt.ValidationJwtTokenException;
import authentication.security.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private UserService userService;
	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	/**
	 *
	 */
	/**
	 *
	 */
	/**
	 *
	 */
	/**
	 *
	 */
	/**
	 *
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = getToken(request);

		if (StringUtils.hasText(token)) {
			try {
				jwtProvider.validateToken(token);
			} catch (ValidationJwtTokenException e) {
				logger.info("Jwt authentication is failed. Jwt token is invalid", e);

				filterChain.doFilter(request, response);

				return;
			}

			String username = jwtProvider.getUsername(token);

			User user = userService.findById(username);

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getId(),
					user.getPassword(), Collections.emptyList());

			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(request, response);
	}

	private String getToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			return token.substring(7);
		}
		return token;
	}

}
