package com.example.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.EProvider;
import com.example.model.ERole;
import com.example.model.User;
import com.example.repository.UserRepository;
import com.example.security.TokenProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@RestController
@RequestMapping("/google")
public class GoogleAndroidController {
	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	private UserRepository userRepo;
	@GetMapping("/checkIdTokenGoogle")
	private String loginWithGoogle(@Autowired NetHttpTransport transport, @Autowired GsonFactory factory, @RequestParam (name="idToken")String idTokenString) throws GeneralSecurityException, IOException {
		GoogleIdTokenVerifier verifier=new GoogleIdTokenVerifier.Builder(transport, factory)
										.setAudience(Collections.singleton("117424537794-affb5362l2mtcooo289g4enjhtgghd49.apps.googleusercontent.com"))
										.build();
		GoogleIdToken idToken=verifier.verify(idTokenString);
		if(idToken!=null) {
			Payload payload=idToken.getPayload();
			// lay thong tin
			String email=payload.getEmail();
			System.out.println(email);
			if(!userRepo.existsByEmail(email)) {
				User u=new User();
				u.setEmail(email);
				u.setName((String)payload.get("name"));
				u.setEnabled(true);
				u.setProvider(EProvider.google);
				u.setRole(ERole.ROLE_USER);
				Date date=new Date();
				u.setCreateAt(date);
				userRepo.save(u);
			}
			String token=tokenProvider.createTokenByEmail(email);
			return token;
		}else {
			return "Invalid ID token";
		}
		
	}
}
