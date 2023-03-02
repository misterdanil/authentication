package authentication.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.client.RestTemplate;

import authentication.security.oauth2.converter.CookieSecurityContextRepository;
import authentication.security.oauth2.converter.OAuth2AuthorizationCodeGrantRequestEntityConverterCustom;
import authentication.service.impl.DaoAndMemoryOAuth2AuthorizedClientService;
import jakarta.servlet.Filter;

@Configuration
public class WebSecurityConfig {
	@Autowired
	private CookieSecurityContextRepository cookieSecurityContextRepository;
	@Autowired
	private RestTemplate codeTokenRestTemplate;
	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;
	@Autowired
	private OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService;
	@Autowired
	@Qualifier("daoAndMemoryOAuth2AuthorizedClientService")
	private OAuth2AuthorizedClientService authorizedClientService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		DefaultAuthorizationCodeTokenResponseClient responseClient = new DefaultAuthorizationCodeTokenResponseClient();
		responseClient.setRequestEntityConverter(new OAuth2AuthorizationCodeGrantRequestEntityConverterCustom());
		responseClient.setRestOperations(codeTokenRestTemplate);

		http.cors().and().exceptionHandling().and().oauth2Login()
				.clientRegistrationRepository(clientRegistrationRepository).authorizationEndpoint()
				.baseUri("/login/oauth2").and().and().addFilterAfter(new Test(), SecurityContextHolderFilter.class)
				.oauth2Login().tokenEndpoint().accessTokenResponseClient(responseClient).and().userInfoEndpoint()
				.userService(oauth2UserService).and().authorizedClientService(authorizedClientService).and()
				.securityContext().securityContextRepository(cookieSecurityContextRepository).and().csrf().disable();

		SecurityFilterChain filterChain = http.build();
		List<Filter> f = filterChain.getFilters();
		return filterChain;
	}
}
