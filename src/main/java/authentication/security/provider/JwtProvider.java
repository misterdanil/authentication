package authentication.security.provider;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import authentication.security.error.jwt.ExpiredJwtTokenException;
import authentication.security.error.jwt.MalformedJwtTokenException;
import authentication.security.error.jwt.SignatureJwtTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import io.jsonwebtoken.security.SignatureException;

@Component
@PropertySource(value = "classpath:jwt.properties")
public class JwtProvider {
	private byte[] secretKey;
	private String algorithm;
	@Resource
	private Environment env;
	private Long expirationTimeMillis;

	@PostConstruct
	public void init() {
		secretKey = env.getProperty("secret_key").getBytes();
		algorithm = env.getProperty("algorithm");
		expirationTimeMillis = System.currentTimeMillis() + Long.valueOf(env.getProperty("exp"));
	}

	public String generateToken(Authentication authentication) {
		Instant expirationDate = generateExpirationDate();

		Key key = Keys.hmacShaKeyFor(secretKey);

		String token = Jwts.builder().setSubject(authentication.getName()).setExpiration(Date.from(expirationDate))
				.signWith(key, SignatureAlgorithm.valueOf(algorithm)).compact();

		return token;
	}

	public boolean validateToken(String token)
			throws ExpiredJwtTokenException, SignatureJwtTokenException, MalformedJwtTokenException {
		try {
			Jwts.parserBuilder().setSigningKey(secretKey).build().parse(token);
		} catch (ExpiredJwtException e) {
			throw new ExpiredJwtTokenException("Exception occurred while validation jwt token. Jwt token is expired",
					e);
		} catch (SignatureException e) {
			throw new SignatureJwtTokenException(
					"Exception occurred while validation jwt token. Jwt token signature is invalid", e);
		} catch (MalformedJwtException e) {
			throw new MalformedJwtTokenException(
					"Exception occurred while validation jwt token. Jwt token is malformed", e);
		}
		return true;
	}

	public String getUsername(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	public Long getExpirationTimeMillis() {
		return expirationTimeMillis;
	}

	private Instant generateExpirationDate() {
		return Instant.now().plusMillis(expirationTimeMillis);
	}

}
