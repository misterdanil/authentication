package authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginRequest {
	@NotBlank
	@Size(max = 128)
	@Pattern(regexp = "^\\w+@\\w+$")
	private String email;
	@NotBlank
	@Size(min = 8, max = 256)
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
