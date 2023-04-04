package authentication.controller;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bebracore.cabinet.service.UserService;

import authentication.dto.AuthenticationResponse;
import authentication.dto.LoginRequest;
import authentication.dto.OtpRequest;
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
import authentication.service.OtpService;
import authentication.service.impl.OtpServiceImpl.CodeExpiredException;
import authentication.service.impl.OtpServiceImpl.CodeNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
public class AuthController extends AbstractController {
	@Autowired
	private AuthService authService;
	@Autowired
	private UserService userService;
	@Autowired
	private OtpService otpService;
	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@PostMapping("/signupf")
	public ResponseEntity<Object> signup(@RequestBody @Valid RegisterRequest registerRequest, BindingResult result) {
		if (result.hasErrors()) {
			return new ResponseEntity<>(createErrorValidationResponse(result), HttpStatus.BAD_REQUEST);
		}

		AuthenticationResponse authResponse;
		try {
			authResponse = authService.signup(registerRequest);
		} catch (UsernameExistException e) {
			result.rejectValue("username", "User with name: " + registerRequest.getUsername() + " already exist");
			return new ResponseEntity<>(createErrorValidationResponse(result), HttpStatus.BAD_REQUEST);
		} catch (EmailExistException e) {
			result.rejectValue("email", "User with email: " + registerRequest.getEmail() + " already exist");
			return new ResponseEntity<>(createErrorValidationResponse(result), HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
	}

	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody(required = false) @Valid LoginRequest loginRequest,
			BindingResult result, HttpServletRequest request) {

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

		return ResponseEntity.ok(authenticationResponse);

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

			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorValidationResponse(result));
		} catch (RefreshTokenExpiredException e) {
			logger.info("Refresh token '{}' not found", refreshTokenRequest.getRefreshToken());

			result.rejectValue("token", "token.expired", "Refresh token has expired");

			return ResponseEntity.status(HttpStatus.GONE).body(createErrorValidationResponse(result));
		}

		return ResponseEntity.ok(createSuccessValidationResponse(authenticationResponse));
	}

	@PutMapping("/password/{email}")
	public ResponseEntity<Void> sendOtp(@PathVariable String email) {

		if (!userService.existsByEmail(email)) {
			return ResponseEntity.notFound().build();
		}

		otpService.save(email);

		return ResponseEntity.created(null).build();
	}

	@PostMapping("/password")
	public ResponseEntity<Object> resetPassword(@RequestBody @Validated OtpRequest request, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(createErrorValidationResponse(result));
		}

		try {
			otpService.resetPassword(request.getCode(), request.getNewPassword());
		} catch (CodeNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (CodeExpiredException e) {
			return ResponseEntity.status(410).build();
		}

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/get")
	@ResponseBody
	public String get() {
		return "got";
	}
}
