package authentication.service.impl;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bebracore.cabinet.service.UserService;

import authentication.dto.Email;
import authentication.generator.OtpGenerator;
import authentication.model.Otp;
import authentication.repository.OtpRepository;
import authentication.service.MailService;
import authentication.service.OtpService;
import authentication.util.SecurityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

@Service
@PropertySource(value = { "classpath:password.properties" })
public class OtpServiceImpl implements OtpService {
	@Autowired
	private OtpRepository otpRepository;
	@Autowired
	private OtpGenerator<Integer> otpGenerator;
	@Autowired
	private MailService mailService;
	@Autowired
	private UserService userService;
	@Resource
	private Environment env;
	private byte[] salt;

	@PostConstruct
	public void init() {
		salt = env.getProperty("salt").getBytes();
	}

	@Override
	public Otp save(String email) {
		String code = otpGenerator.generate(10);

		if (otpRepository.existsByCode(code)) {
			return save(email);
		}

		Otp otp = new Otp();
		otp.setEmail(email);
		otp.setCode(code);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, 1);

		otp.setExpired(calendar.getTime());

		Email recipient = new Email();
		recipient.setBody(
				"Был послан запрос на смену пароля. Введите в поле с кодом это значени для его сброса: " + code);
		recipient.setRecipient(email);
		recipient.setSubject("Сброс пароля");

		mailService.send(recipient);

		return otpRepository.save(otp);
	}

	@Override
	public boolean existByCode(String code) {
		return otpRepository.existsByCode(code);
	}

	@Override
	public void resetPassword(String code, String password) throws CodeNotFoundException, CodeExpiredException {
		Otp otp = otpRepository.findByCode(code);

		if (otp == null) {
			throw new CodeNotFoundException("Код не найден");
		}
		if (otp.getExpired().before(new Date())) {
			throw new CodeExpiredException("Срок действия кода истёк");
		}

		userService.updatePassword(otp.getEmail(), SecurityUtil.getSecurePassword(password, salt));
	}

	public class CodeNotFoundException extends Exception {

		public CodeNotFoundException(String message, Throwable cause) {
			super(message, cause);
		}

		public CodeNotFoundException(String message) {
			super(message);
		}
	}

	public class CodeExpiredException extends Exception {

		public CodeExpiredException(String message, Throwable cause) {
			super(message, cause);
		}

		public CodeExpiredException(String message) {
			super(message);
		}

	}

}
