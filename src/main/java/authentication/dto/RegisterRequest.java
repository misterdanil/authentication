package authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
	@NotBlank(message = "Введите ваш псевдоним")
	@Size(max = 128, message="Превышено максимальное количество символов: 128")
	@Pattern(regexp = "^[а-яА-Я]{1,128}|[a-zA-Z]{1,128}$")
	private String username;
	@NotBlank(message = "Введите электронную почту")
	@Size(max = 128, message = "Превышено максимальное количество символов: 128")
	private String email;
	@NotBlank(message = "Введите пароль")
	@Size(min = 8, message = "Пароль должен состоять из минимум 8 символов")
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

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
