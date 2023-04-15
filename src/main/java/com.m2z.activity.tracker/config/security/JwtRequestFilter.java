package com.m2z.activity.tracker.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtRequestFilter extends UsernamePasswordAuthenticationFilter {
	@Autowired
	UserDetailsService userDetailsService;
	@Autowired
	private TokenProvider tokenProvider;

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String JWT_PREFIX = "Bearer ";

	private String resolveToken(HttpServletRequest request) {
		String authToken = request.getHeader(AUTHORIZATION_HEADER);
		if(authToken != null) {
			if (authToken.startsWith(JWT_PREFIX)) {
				return authToken.substring(7);
			}
		}
		return null;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) req;
		String authToken = resolveToken(httpRequest);
		if(authToken != null) {
			String username = tokenProvider.getUsername(authToken);

			if ((username != null) && (SecurityContextHolder.getContext().getAuthentication() == null)) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				if (tokenProvider.validateToken(authToken, userDetails)) {
					UsernamePasswordAuthenticationWithToken authentication = new UsernamePasswordAuthenticationWithToken(
							userDetails, null, userDetails.getAuthorities(), authToken);
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}
		super.doFilter(req, res, chain);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

	}
}
