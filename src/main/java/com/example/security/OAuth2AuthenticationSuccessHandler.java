package com.example.security;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;


@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
	private final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
	@Autowired
	private TokenProvider tokenProvider;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		String targetUrl=determineTarget(request, response, authentication);
		
		if(response.isCommitted()) {
			logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
		}
		clearAuthenticationAttributes(request, response);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		super.clearAuthenticationAttributes(request);
		CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
		
	}

	private String determineTarget(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		Optional<String>redirectUri=CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
									.map(Cookie::getValue);
		String targetUrl=redirectUri.get();
		String token = tokenProvider.createToken(authentication);
		return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
	}
	
}
