package authentication.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import authentication.model.RefreshToken;
import authentication.repository.RefreshTokenRepository;
import authentication.security.error.auth.RefreshTokenNotFoundException;
import authentication.service.RefreshTokenService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

@Service
@PropertySource("classpath:refresh_token.properties")
public class RefreshTokenServiceImpl implements RefreshTokenService {
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	private Integer expirationTimeHours;
	@Resource
	private Environment env;

	@PostConstruct
	public void init() {
		expirationTimeHours = Integer.valueOf(env.getProperty("exp"));
	}

	@Override
	public RefreshToken findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}

	@Override
	public RefreshToken save(RefreshToken refreshToken) {
		return refreshTokenRepository.save(refreshToken);
	}

	@Override
	public RefreshToken generateRefreshToken() {
		RefreshToken refreshToken = new RefreshToken();

		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setIssuedAt(Instant.now());
		refreshToken.setExpiresAt(generateExpiresAt());

		return refreshToken;
	}

	private Instant generateExpiresAt() {
		return Instant.now().plus(expirationTimeHours, ChronoUnit.HOURS);
	}

	@Override
	public void removeByToken(String token) {
		refreshTokenRepository.removeByToken(token);
	}
}
