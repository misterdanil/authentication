package authentication.security.error.jwt;

public class ExpiredJwtTokenException extends ValidationJwtTokenException {

	public ExpiredJwtTokenException(String message, Throwable cause) {
		super(message, cause);
	}
}
