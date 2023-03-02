package authentication.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestTemplate;

import authentication.security.oauth2.converter.OAuth2JsonTokenConverter;
import authentication.security.oauth2.converter.OAuth2ResponseClientConverter;

@Configuration
public class OAuth2Config {
	private static final String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";
	private List<String> clients = Arrays.asList("google", "vk");

	@Autowired
	private Environment env;

	@Bean
	public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
		DefaultOAuth2UserService oauth2UserService = new DefaultOAuth2UserService();
		oauth2UserService.setRestOperations(userInfoRestTemplate());

		return oauth2UserService;
	}

	@Bean
	public RestTemplate codeTokenRestTemplate() {
		RestTemplate restTemplate = new RestTemplate(
				Arrays.asList(new FormHttpMessageConverter(), new OAuth2JsonTokenConverter()));
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		return restTemplate;
	}

	private RestTemplate userInfoRestTemplate() {
		RestTemplate restTemplate = new RestTemplate(
				Arrays.asList(new ByteArrayHttpMessageConverter(), new StringHttpMessageConverter(),
						new AllEncompassingFormHttpMessageConverter(), new OAuth2ResponseClientConverter()));
		restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
		return restTemplate;
	}

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		List<ClientRegistration> clientRegistrations = new ArrayList<>();

		clients.forEach(client -> {
			ClientRegistration clientRegistration = getClientRegistration(client);
			if (clientRegistration != null) {
				clientRegistrations.add(clientRegistration);
			}
		});

		return new InMemoryClientRegistrationRepository(clientRegistrations);
	}

	@Bean
	public InMemoryOAuth2AuthorizedClientService inMemoryOAuth2AuthorizedClientService() {
		return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository());
	}

	private ClientRegistration getClientRegistration(String client) {
		String clientId = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-id");
		String secretId = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-secret");
		String redirectUri = env.getProperty(CLIENT_PROPERTY_KEY + client + ".redirect-uri");

		if (clientId == null || secretId == null) {
			return null;
		}

		if (client.equals("google")) {
			return CommonOAuth2Provider.GOOGLE.getBuilder(client).clientId(clientId).clientSecret(secretId)
					.redirectUri(redirectUri).build();
		} else if (client.equals("vk")) {
			return ClientRegistration.withRegistrationId(client).clientId(clientId).clientSecret(secretId)
					.redirectUri(redirectUri).authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
					.tokenUri("https://oauth.vk.com/access_token")
					.authorizationUri(env.getProperty(CLIENT_PROPERTY_KEY + client + ".authorization-uri"))
					.scope("email", "friends").userInfoUri("https://api.vk.com/method/users.get?v=5.131&fields=sex")
					.userNameAttributeName("id").build();
		} else {
			return null;
		}
	}
}
