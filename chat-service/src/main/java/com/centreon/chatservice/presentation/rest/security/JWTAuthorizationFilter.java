package com.centreon.chatservice.presentation.rest.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.centreon.chatservice.application.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Jwt authorization filter.
 *
 * @author ghazi
 */
public class JWTAuthorizationFilter extends OncePerRequestFilter
{
	private static final Logger LOGGER = LogManager.getLogger();
	private final UserService userService;

	public JWTAuthorizationFilter(UserService userService)
	{
		this.userService = userService;
	}

	@Override
	public void doFilterInternal(HttpServletRequest request,
		HttpServletResponse response, FilterChain chain)
		throws IOException, ServletException
	{
		// If we are already authenticated, don't try to reauthenticate
		Authentication authentication =
			SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			chain.doFilter(request, response);
			return;
		}

		// Verify that the request holds a Bearer authentication
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorization == null || !authorization.startsWith("Bearer ")) {
			chain.doFilter(request, response);
			return;
		}

		// Extract the authentication principal from the JWT token
		String token = authorization.replace("Bearer ", "");
		try {
			authentication = authenticate(token);

			// Persist the authentication to the global security context
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (JWTDecodeException e) {
			LOGGER.error("Failed to decode jwt token: {}", token, e);
		}

		chain.doFilter(request, response);
	}

	private Authentication authenticate(String token)
	{
		return userService.checkAuthentication(token).map(principal -> {
			// The returned authentication token needs to be trusted,
			// this is why we set its authenticated attribute to true
			PreAuthenticatedAuthenticationToken authenticationToken =
				new PreAuthenticatedAuthenticationToken(principal, "");
			authenticationToken.setAuthenticated(true);
			return authenticationToken;
		}).orElse(null);
	}
}
