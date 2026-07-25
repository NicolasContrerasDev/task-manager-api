package com.portfolio.tareas.tareas_api.services;

import com.portfolio.tareas.tareas_api.models.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private final String secret;
	private final long expirationMs;

	public JwtService(
		@Value("${security.jwt.secret}") String secret,
		@Value("${security.jwt.expiration-ms}") long expirationMs
	) {
		this.secret = secret;
		this.expirationMs = expirationMs;
	}

	public String generateToken(AppUser user) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + expirationMs);
		Map<String, Object> claims = Map.of(
			"userId", user.getId(),
			"role", user.getRole().name()
		);

		return Jwts.builder()
			.claims(claims)
			.subject(user.getUsername())
			.issuedAt(now)
			.expiration(expiration)
			.signWith(getSigningKey())
			.compact();
	}

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	public long getExpirationMs() {
		return expirationMs;
	}

	private boolean isTokenExpired(String token) {
		return extractAllClaims(token).getExpiration().before(new Date());
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
			.verifyWith(getSigningKey())
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}
}
