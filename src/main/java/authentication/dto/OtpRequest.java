package authentication.dto;

import jakarta.validation.constraints.NotBlank;

public class OtpRequest {
	@NotBlank
	private String code;
	@NotBlank
	private String newPassword;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
