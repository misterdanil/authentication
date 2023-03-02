package authentication.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecurityUtil {
	public static String getSecurePassword(String password, byte[] salt) {
		String alghorithm = "SHA-256";
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(alghorithm);
			messageDigest.update(salt);

			byte[] bytes = messageDigest.digest(password.getBytes());

			StringBuilder builder = new StringBuilder();

			for (byte b : bytes) {
				builder.append(String.format("%02x", b));
			}

			return builder.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(
					String.format(
							"Exception occurred while getting message digest by algorithm %s. Couldn't get instance"),
					e);
		}
	}

	public static byte[] getSalt() {
		SecureRandom secureRandom = new SecureRandom();
		byte[] salt = new byte[16];
		secureRandom.nextBytes(salt);
		return salt;
	}
}
