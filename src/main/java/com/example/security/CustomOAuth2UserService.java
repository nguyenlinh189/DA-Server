package com.example.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.dto.CurrentUser;
import com.example.exception.OAuth2AuthenticationProcessingException;
import com.example.model.EProvider;
import com.example.model.ERole;
import com.example.model.User;
import com.example.repository.UserRepository;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) {
		OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
		
        try {
        	return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
	}

	private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
		Map<String, Object> attributes = oAuth2User.getAttributes();
		String name = (String) attributes.get("name");
		String email = (String) attributes.get("email");
		User userGoogle = new User();
		userGoogle.setName(name);
		userGoogle.setEmail(email);

		if (StringUtils.isEmpty(userGoogle.getEmail())) {
			throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
		}
		
		User user = userRepository.findByEmail(userGoogle.getEmail());
		if (user != null) {
			if (!user.getProvider().equals(EProvider.google)) {
				throw new OAuth2AuthenticationProcessingException(
						"Bạn đã đăng kí với tài khoản " + user.getProvider() + ". Hay sử dụng tài khoản  "
								+ user.getProvider() + " để login.");
			}
			user = updateExistingUser(user, userGoogle);
		} else {
			user = registerNewUser(userGoogle);
		}
		CurrentUser.user=user;
		return UserPrincipal.create(user, oAuth2User.getAttributes());
	}

	private User registerNewUser(User oAuth2UserInfo) {
		User user = new User();
		user.setProvider(EProvider.google);
		user.setRole(ERole.ROLE_USER);
		user.setName(oAuth2UserInfo.getName());
		user.setEmail(oAuth2UserInfo.getEmail());
		user.setEnabled(true);
		
		return userRepository.save(user);
	}

	private User updateExistingUser(User existingUser, User oAuth2UserInfo) {
		existingUser.setName(oAuth2UserInfo.getName());
		return userRepository.save(existingUser);
	}
}
