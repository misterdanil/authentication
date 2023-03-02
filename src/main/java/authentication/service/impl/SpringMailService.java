package authentication.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import authentication.builder.MailContentBuilder;
import authentication.dto.Email;
import authentication.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class SpringMailService implements MailService {
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private MailContentBuilder mailContentBuilder;
	private boolean isHtml;

	public SpringMailService(boolean isHtml) {
		this.isHtml = isHtml;
	}

	public SpringMailService() {
		isHtml = true;
	}

	public void send(Email email) {
		MimeMessage message = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

		try {
			helper.setTo(email.getRecipient());
			helper.setFrom("jon_working@mail.ru");
			helper.setText(mailContentBuilder.build(email.getBody()), isHtml);
			helper.setSubject(email.getSubject());
		} catch (MessagingException e) {
			throw new RuntimeException("Could not send message to email: " + email.getRecipient(), e);
		}

		mailSender.send(message);
	}
}
