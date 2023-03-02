package authentication.security.oauth2.converter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OAuth2JsonTokenConverter extends OAuth2AccessTokenResponseHttpMessageConverter {

	private ObjectMapper mapper = new ObjectMapper();

	@Override
	protected OAuth2AccessTokenResponse readInternal(Class<? extends OAuth2AccessTokenResponse> clazz,
			HttpInputMessage inputMessage) throws HttpMessageNotReadableException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			inputMessage.getBody().transferTo(os);
		} catch (IOException e1) {
			// log
		}
		HttpInputMessage in = new HttpInputMessage() {

			@Override
			public HttpHeaders getHeaders() {
				return inputMessage.getHeaders();
			}

			@Override
			public InputStream getBody() throws IOException {
				return new ByteArrayInputStream(os.toByteArray());
			}
		};

		try {
			return super.readInternal(clazz, in);
		} catch (HttpMessageNotReadableException e) {
			// log
		}

		String body = null;
		body = new String(os.toByteArray());
		JsonNode tokenNode;
		try {
			tokenNode = mapper.readTree(body);
		} catch (JsonProcessingException e) {
			// log
			return null;
		}

		String tokenValue = tokenNode.get("access_token").asText();
		long expiresIn = Long.valueOf(tokenNode.get("expires_in").asText());

		OAuth2AccessTokenResponse accessTokenResponse = OAuth2AccessTokenResponse.withToken(tokenValue)
				.expiresIn(expiresIn).tokenType(TokenType.BEARER).build();
		return accessTokenResponse;
	}

	@Override
	protected void writeInternal(OAuth2AccessTokenResponse tokenResponse, HttpOutputMessage outputMessage)
			throws HttpMessageNotWritableException {
		super.writeInternal(tokenResponse, outputMessage);
	}

}
