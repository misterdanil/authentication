package authentication.security.oauth2.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;

public class OAuth2ResponseClientConverter extends MappingJackson2HttpMessageConverter {

	@Override
	public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		String body;
		try {
			body = new String(inputMessage.getBody().readAllBytes());
		} catch (IOException e) {
			// log
			return null;
		}

		JavaType javaType = getJavaType(type, contextClass);
		ObjectReader reader = getObjectMapper().reader().forType(javaType);

		JsonNode responseNode;
		try {
			responseNode = getObjectMapper().readTree(body);
		} catch (JsonProcessingException e) {
			// log
			return null;
		}

		// checking if vk
		if (responseNode.has("response") && responseNode.get("response").isArray()) {
			Map<String, Object> userInfo = getObjectMapper().convertValue(responseNode.get("response").get(0),
					new TypeReference<Map<String, Object>>() {
					});

			return userInfo;
		}
		return getObjectMapper().convertValue(responseNode, new TypeReference<Map<String, Object>>() {
		});
	}
}
