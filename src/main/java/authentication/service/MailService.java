package authentication.service;

import authentication.dto.Email;

public interface MailService {
	void send(Email email);
}
