package authentication.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class RefreshToken {
	@Id
	private String id;
	private String token;
	private Instant issuedAt;
	private Instant expiresAt;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Instant getIssuedAt() {
		return issuedAt;
	}

	public void setIssuedAt(Instant issuedAt) {
		this.issuedAt = issuedAt;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

}
