package authentication.security.error.jwt;

public class SignatureJwtTokenException extends ValidationJwtTokenException {

	public SignatureJwtTokenException(String message, Throwable cause) {
		super(message, cause);
	}

}
