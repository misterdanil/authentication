package authentication.generator;

public interface OtpGenerator<T> {
	String generate(T source);
}
