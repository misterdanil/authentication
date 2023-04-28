package authentication.dto;

import authentication.model.RefreshToken;

public class AuthenticationResponse {
	private String id;
	private String username;
	private String accessToken;
	private RefreshToken refreshToken;

	public AuthenticationResponse(String id, String username, String accessToken, RefreshToken refreshToken) {
		super();
		this.id = id;
		this.username = username;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public AuthenticationResponse(String username, String accessToken, RefreshToken refreshToken) {
		super();
		this.username = username;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public RefreshToken getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(RefreshToken refreshToken) {
		this.refreshToken = refreshToken;
	}

}
