package authentication.service;

import authentication.model.Otp;
import authentication.service.impl.OtpServiceImpl.CodeExpiredException;
import authentication.service.impl.OtpServiceImpl.CodeNotFoundException;

public interface OtpService {

	Otp save(String email);

	boolean existByCode(String code);

	void resetPassword(String code, String password) throws CodeNotFoundException, CodeExpiredException;
}
