package authentication.model;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Otp {
	private String id;
	private String email;
	private String code;
	private Date expired;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getExpired() {
		return expired;
	}

	public void setExpired(Date expired) {
		this.expired = expired;
	}

}
