package authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
	@NotBlank
	@Size(max = 128)
	@Pattern(regexp = "^[а-яА-Я]{1,128}|[a-zA-Z]{1,128}$")
	private String username;
	@NotBlank
	@Size(max = 128)
	@Pattern(regexp = "^\\w+@\\w+$")
	private String email;
	@NotBlank
	@Size(min = 8, max = 256)
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
