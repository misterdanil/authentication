package authentication.service.impl;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bebracore.cabinet.model.User;
import com.bebracore.cabinet.model.VerificationToken;
import com.bebracore.cabinet.repository.UserRepository;

import authentication.dto.AuthenticationResponse;
import authentication.dto.Email;
import authentication.dto.LoginRequest;
import authentication.dto.RefreshTokenRequest;
import authentication.dto.RegisterRequest;
import authentication.model.RefreshToken;
import authentication.repository.VerificationTokenRepository;
import authentication.security.error.auth.EmailExistException;
import authentication.security.error.auth.EmailNotFoundException;
import authentication.security.error.auth.ExpiredVerificationTokenException;
import authentication.security.error.auth.PasswordMatchedException;
import authentication.security.error.auth.RefreshTokenExpiredException;
import authentication.security.error.auth.RefreshTokenNotFoundException;
import authentication.security.error.auth.UsernameExistException;
import authentication.security.provider.JwtProvider;
import authentication.service.AuthService;
import authentication.service.MailService;
import authentication.service.RefreshTokenService;
import authentication.util.SecurityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

@Service
@PropertySource(value = { "classpath:verification.properties", "classpath:password.properties" })
public class AuthServiceImpl implements AuthService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private VerificationTokenRepository verificationTokenRepository;
	@Autowired
	private MailService mailService;
	@Resource
	private Environment env;
	@Autowired
	private JwtProvider jwtProvider;
	@Autowired
	private RefreshTokenService refreshTokenService;
	private byte[] salt;
	private long expMail;
	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

	@PostConstruct
	public void init() {
		salt = env.getProperty("salt").getBytes();
		expMail = Long.valueOf(env.getProperty("exp_mail"));
	}

	public AuthenticationResponse signup(RegisterRequest registerRequest)
			throws EmailExistException, UsernameExistException {
		existEmail(registerRequest.getEmail());
		existUsername(registerRequest.getUsername());

		User user = new User();

		user.setUsername(registerRequest.getUsername());

		user.setPassword(SecurityUtil.getSecurePassword(registerRequest.getPassword(), salt));
		user.setEmail(registerRequest.getEmail());

		userRepository.save(user);

		VerificationToken verificationToken = generateVerificationToken(user);

		verificationTokenRepository.save(verificationToken);

		Email email = new Email();

		email.setBody("Activate account, man by this link: " + "http://localhost:8080/accountVerification/"
				+ verificationToken.getToken());
		email.setRecipient(registerRequest.getEmail());
		email.setSubject("Activation");

		mailService.send(email);

		LoginRequest loginRequest = new LoginRequest();
		loginRequest.setEmail(registerRequest.getEmail());
		loginRequest.setPassword(registerRequest.getPassword());

		try {
			return login(loginRequest);
		} catch (EmailNotFoundException | PasswordMatchedException e) {
			throw new RuntimeException("It's impossible", e);
		}
	}

	public VerificationToken generateVerificationToken(User user) {
		String token = UUID.randomUUID().toString();

		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		verificationToken.setExpirationDate(Instant.now().plusMillis(expMail));

		return verificationToken;
	}

	public void verifyAccount(String token) throws ExpiredVerificationTokenException {
		VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

		validToken(verificationToken);
		fetchAndEnableUser(verificationToken);

		verificationTokenRepository.delete(verificationToken);
	}

	@Override
	public AuthenticationResponse login(LoginRequest loginRequest)
			throws EmailNotFoundException, PasswordMatchedException {

		User user = userRepository.findByEmail(loginRequest.getEmail());
		if(user == null) {
			throw new EmailNotFoundException("test");
		}

		String password = user.getPassword();
		String hashedPassword = SecurityUtil.getSecurePassword(loginRequest.getPassword(), salt);
		if (!user.getPassword().equals(hashedPassword)) {
			throw new PasswordMatchedException(
					String.format("Exception occurred while authorizating user. Password '%s' and '%s' aren't matched",
							password, hashedPassword));
		}

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getId(),
				hashedPassword, Collections.emptyList());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String accessToken = jwtProvider.generateToken(authentication);

		RefreshToken refreshToken = refreshTokenService.generateRefreshToken();

		refreshToken = refreshTokenService.save(refreshToken);

		return new AuthenticationResponse(user.getUsername(), accessToken, refreshToken);
	}

	@Override
	public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest)
			throws RefreshTokenNotFoundException, RefreshTokenExpiredException {
		RefreshToken refreshToken;

		refreshToken = refreshTokenService.findByToken(refreshTokenRequest.getRefreshToken());

		refreshTokenService.removeByToken(refreshToken.getToken());

		if (refreshToken.getExpiresAt().isAfter(Instant.now())) {
			throw new RefreshTokenExpiredException(
					"Exception occurred while validation refresh token. Refresh token is expired");
		}

		String accessToken = jwtProvider.generateToken(SecurityContextHolder.getContext().getAuthentication());

		refreshToken = refreshTokenService.generateRefreshToken();

		refreshToken = refreshTokenService.save(refreshToken);

		return new AuthenticationResponse(refreshTokenRequest.getUsername(), accessToken, refreshToken);
	}

	private void fetchAndEnableUser(VerificationToken verificationToken) {
		User user = userRepository.findByUsername(verificationToken.getUser().getUsername());

		user.setEnabled(true);

		userRepository.save(user);
	}

	private void validToken(VerificationToken verificationToken) throws ExpiredVerificationTokenException {
		if (Instant.now().isAfter(verificationToken.getExpirationDate())) {
			throw new ExpiredVerificationTokenException(
					"Exception occurred while validation verification token. Verification token is expirated");
		}
	}

	private void existEmail(String email) throws EmailExistException {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			throw new EmailExistException(
					"Exception occurred while checking user for the existence of the email. User exist with email: "
							+ email);
		}
	}

	private void existUsername(String username) throws UsernameExistException {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			throw new UsernameExistException(
					"Exception occurred while checking user for the existence of the username. User exist with username: "
							+ username);
		}
	}

	public static void main(String[] args) {
		Instant n = Instant.now().plusMillis(Long.valueOf("600000"));
		System.out.println();
	}
}
