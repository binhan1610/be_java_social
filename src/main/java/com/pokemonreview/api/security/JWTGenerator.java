package com.pokemonreview.api.security;

import java.util.Date;

import com.pokemonreview.api.models.UserEntity;
import com.pokemonreview.api.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
//import java.security.KeyPair;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JWTGenerator {

	@Autowired
	private  UserRepository userRepository;
	//private static final KeyPair keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
	private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	
	public Pair<String, Date> generateToken(Authentication authentication) {
		String username = authentication.getName();
		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);
		
		String token = Jwts.builder()
				.setSubject(username)
				.setIssuedAt( new Date())
				.setExpiration(expireDate)
				.signWith(key,SignatureAlgorithm.HS512)
				.compact();
		return Pair.of(token, expireDate);
	}
	public String getUsernameFromJWT(String token){
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		return claims.getSubject();
	}

	public boolean validateToken(String token) {

			Claims claims = Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody();

			// Kiểm tra xem token đã hết hạn chưa
			Date expiration = claims.getExpiration();
			Date now = new Date();
			if (expiration != null && expiration.before(now)) {
				throw new AuthenticationCredentialsNotFoundException("JWT has expired");
			}

			// So sánh token với dữ liệu trong cơ sở dữ liệu (ví dụ: lấy token từ db và so sánh)
			String username=this.getUsernameFromJWT(token);
			UserEntity user= this.userRepository.findByUsername(username).orElseThrow(()->new AuthenticationCredentialsNotFoundException("User not found"));
			String tokenFromDB = user.getToken();
			if (!token.equals(tokenFromDB)) {
				throw new AuthenticationCredentialsNotFoundException("JWT does not match token in database");
			}

			return true;
	}

}
