package authentication.security.oauth2.converter;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class OAuth2AuthorizationCodeGrantRequestEntityConverterCustom
		extends OAuth2AuthorizationCodeGrantRequestEntityConverter {

	@Override
	public RequestEntity<?> convert(OAuth2AuthorizationCodeGrantRequest authorizationGrantRequest) {
		ClientRegistration clientRegistration = authorizationGrantRequest.getClientRegistration();
		if (clientRegistration.getRegistrationId().equals("vk")) {
			URI uri = UriComponentsBuilder
					.fromUriString(authorizationGrantRequest.getClientRegistration().getProviderDetails().getTokenUri())
					.queryParam("client_id", clientRegistration.getClientId())
					.queryParam("client_secret", clientRegistration.getClientSecret())
					.queryParam("redirect_uri", clientRegistration.getRedirectUri())
					.queryParam("code",
							authorizationGrantRequest.getAuthorizationExchange().getAuthorizationResponse().getCode())
					.build().toUri();
			return new RequestEntity<>(null, null, HttpMethod.GET, uri);
		}
		return super.convert(authorizationGrantRequest);
	}

}
