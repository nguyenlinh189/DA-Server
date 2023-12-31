package com.example.security;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

// tao va xác thực jwt
@Component
@Slf4j
public class TokenProvider {
	private String JWT_SECRET="04ca023b39512e46d0c2cf4b48d5aac61d34302994c87ed4eff225dcf3b0a218739f3897051a057f9b846a69ea2927a587044164b7bae5e1306219d50b588cb1";
	private int JWT_EXPIRATION=24*60*60*1000;
	// tao jwt tu thong tin cua user
		public String createToken(Authentication authentication) {
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			return createTokenByEmail(userPrincipal.getEmail());
		}
		// lay thong tin user tu token
		public String getJwtFromRequest(String token) {
			Claims claims=Jwts.parser().setSigningKey(JWT_SECRET)
					.parseClaimsJws(token).getBody();
			return claims.getSubject();
		}
		// validate thong tin cuar jwt
		public boolean validateToken(String authToken) {
			try {
				Jwts.parser().setSigningKey(JWT_SECRET)
				.parseClaimsJws(authToken);
				return true;
			}catch(SignatureException ex) {
				log.error("Invalid JWT signature");
			}catch(MalformedJwtException ex) {
				log.error("Invalid JWT token");
			}catch(ExpiredJwtException ex) {
				log.error("Expired JWT token");
			}catch(UnsupportedJwtException ex) {
				log.error("Unsupported Jwt token");
			}catch(IllegalArgumentException ex) {
				log.error("JWT claims string is empty");
			}
			return false;
		}
		
		public String createTokenByEmail(String email) {
			Date now=new Date();
			Date dateExpired=new Date(now.getTime()+JWT_EXPIRATION);
			System.out.println(dateExpired);
			// tao ra chuoi jwt tu thong tin cua email
			return Jwts.builder().setSubject(email)
					.setIssuedAt(now)
					.setExpiration(dateExpired)
					.signWith(SignatureAlgorithm.HS512, JWT_SECRET).compact();
		}
}
