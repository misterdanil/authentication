package authentication.security.error.jwt;

public class MalformedJwtTokenException extends ValidationJwtTokenException {

	public MalformedJwtTokenException(String message, Throwable cause) {
		super(message, cause);
	}

}
