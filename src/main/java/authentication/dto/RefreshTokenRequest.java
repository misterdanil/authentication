package authentication.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {
	@NotBlank
	private String username;
	@NotBlank
	private String refreshToken;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
