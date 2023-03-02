package authentication.service;

import authentication.model.RefreshToken;
import authentication.security.error.auth.RefreshTokenNotFoundException;

public interface RefreshTokenService {

	RefreshToken generateRefreshToken();

	RefreshToken save(RefreshToken refreshToken);

	RefreshToken findByToken(String token);

	void removeByToken(String token);

}
