package authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginRequest {
	@NotBlank(message = "Введите эл. почту")
	@Size(max = 128)
	private String email;
	@NotBlank(message = "Введите пароль")
	@Size(min = 8, message="Пароль должен содержать минимум 8 символов")
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
