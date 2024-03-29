package authentication.service;

import authentication.dto.AuthenticationResponse;
import authentication.dto.LoginRequest;
import authentication.dto.RefreshTokenRequest;
import authentication.dto.RegisterRequest;
import authentication.security.error.auth.EmailExistException;
import authentication.security.error.auth.EmailNotFoundException;
import authentication.security.error.auth.ExpiredVerificationTokenException;
import authentication.security.error.auth.PasswordMatchedException;
import authentication.security.error.auth.RefreshTokenExpiredException;
import authentication.security.error.auth.RefreshTokenNotFoundException;
import authentication.security.error.auth.UsernameExistException;
import authentication.service.error.VerificationTokenNotFoundException;

public interface AuthService {
	AuthenticationResponse signup(RegisterRequest registerRequest) throws EmailExistException, UsernameExistException;

	void verifyAccount(String token) throws ExpiredVerificationTokenException, VerificationTokenNotFoundException;

	AuthenticationResponse login(LoginRequest loginRequest) throws EmailNotFoundException, PasswordMatchedException;

	AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest)
			throws RefreshTokenNotFoundException, RefreshTokenExpiredException;
}
