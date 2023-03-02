package authentication.controller;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import authentication.dto.AuthenticationResponse;
import authentication.dto.LoginRequest;
import authentication.dto.RefreshTokenRequest;
import authentication.dto.RegisterRequest;
import authentication.dto.ValidatedResponse;
import authentication.security.error.auth.EmailExistException;
import authentication.security.error.auth.EmailNotFoundException;
import authentication.security.error.auth.ExpiredVerificationTokenException;
import authentication.security.error.auth.PasswordMatchedException;
import authentication.security.error.auth.RefreshTokenExpiredException;
import authentication.security.error.auth.RefreshTokenNotFoundException;
import authentication.security.error.auth.UsernameExistException;
import authentication.service.AuthService;
import authentication.service.RefreshTokenService;
import jakarta.validation.Valid;

@Controller
public class AuthController extends AbstractController {
	@Autowired
	private AuthService authService;
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@PostMapping("/signupf")
	public ResponseEntity<ValidatedResponse> signup(@RequestBody @Valid RegisterRequest registerRequest,
			BindingResult result) {
		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorValidationResponse(result), HttpStatus.BAD_REQUEST);
		}

		try {
			authService.signup(registerRequest);
		} catch (UsernameExistException e) {
			result.rejectValue("username", "User with name: " + registerRequest.getUsername() + " already exist");
			return new ResponseEntity<>(createErrorValidationResponse(result), HttpStatus.BAD_REQUEST);
		} catch (EmailExistException e) {
			result.rejectValue("email", "User with email: " + registerRequest.getEmail() + " already exist");
			return new ResponseEntity<>(createErrorValidationResponse(result), HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<ValidatedResponse> login(@RequestBody @Valid LoginRequest loginRequest,
			BindingResult result) {

		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorValidationResponse(result), HttpStatus.BAD_REQUEST);
		}

		AuthenticationResponse authenticationResponse;

		try {
			authenticationResponse = authService.login(loginRequest);
		} catch (EmailNotFoundException e) {
			result.rejectValue("email", "email.notExist", "Аутентификационные данные неверны");
			return new ResponseEntity<>(createErrorValidationResponse(result), HttpStatus.BAD_REQUEST);
		} catch (PasswordMatchedException e) {
			result.rejectValue("password", "password.matched", "Аутентификационные данные неверны");
			return new ResponseEntity<>(createErrorValidationResponse(result), HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.ok(createSuccessValidationResponse(authenticationResponse));

	}

	@GetMapping("/accountVerification/{token}")
	public ResponseEntity<ValidatedResponse> verifyAccount(@PathVariable String token) {
		BindingResult result = new MapBindingResult(new HashMap<>(), "verificationToken");

		try {
			authService.verifyAccount(token);
		} catch (ExpiredVerificationTokenException e) {
			result.rejectValue("token", "Срок токена истёк");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createErrorValidationResponse(result));
		}
		return new ResponseEntity<ValidatedResponse>(HttpStatus.CREATED);
	}

	@PostMapping("/refresh/token")
	public ResponseEntity<ValidatedResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest,
			BindingResult result) {

		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(createErrorValidationResponse(result));
		}

		AuthenticationResponse authenticationResponse;

		try {
			authenticationResponse = authService.refreshToken(refreshTokenRequest);

		} catch (RefreshTokenNotFoundException e) {
			logger.info("Refresh token '{}' expired", refreshTokenRequest.getRefreshToken());

			result.rejectValue("token", "token.notFound", "Refresh token hasn't found");

			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorValidationResponse(result));
		} catch (RefreshTokenExpiredException e) {
			logger.info("Refresh token '{}' not found", refreshTokenRequest.getRefreshToken());

			result.rejectValue("token", "token.expired", "Refresh token has expired");

			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorValidationResponse(result));
		}

		return ResponseEntity.ok(createSuccessValidationResponse(authenticationResponse));
	}
	
	@GetMapping("/test")
	@ResponseBody
	public String test() {
		return "Hello";
	}
}
