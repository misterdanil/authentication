package authentication.security.oauth2.converter;

import java.time.Instant;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CookieSecurityContextRepository implements SecurityContextRepository {
	private static final String ACCESS_TOKEN_NAME = "access_token";
	private static final String EXPIRES_IN_NAME = "expiresIn";
	private static final String REGISTRATION_ID_NAME = "registrationId";
	private static final String ISSUED_AT_NAME = "issuedAt";
	private static final String ID_TOKEN_NAME = "idToken";
	private static final String EXPIRES_ID_NAME = "expiresInId";
	private static final String ISSUED_ID_NAME = "issuedAtId";
	@Autowired
	@Qualifier("daoAndMemoryOAuth2AuthorizedClientService")
	private OAuth2AuthorizedClientService authorizedClientService;
	private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
			.getContextHolderStrategy();
	@Autowired
	private OAuth2UserService<OAuth2UserRequest, OAuth2User> userService;
	private OidcUserService oidcUserService;
	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

	@Override
	public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
		Cookie[] cookies = requestResponseHolder.getRequest().getCookies();
		if (cookies == null) {
			return null;
		}

		HttpServletRequest request = requestResponseHolder.getRequest();

		if (containsContext(request)) {
			String registrationId = getCookieValue(REGISTRATION_ID_NAME, request);
			String accessToken = getCookieValue(ACCESS_TOKEN_NAME, request);
			String expiresIn = getCookieValue(EXPIRES_IN_NAME, request);
			String issuedAt = getCookieValue(ISSUED_AT_NAME, request);
//			if (registrationId.equals("vk")) {
			OAuth2User oauth2User = userService
					.loadUser(new OAuth2UserRequest(clientRegistrationRepository.findByRegistrationId(registrationId),
							new OAuth2AccessToken(TokenType.BEARER, accessToken,
									Instant.ofEpochSecond(Long.valueOf(issuedAt)),
									Instant.ofEpochSecond(Long.valueOf(expiresIn)))));
//			}
//			else {
//				String idToken = getCookieValue(ID_TOKEN_NAME, request);
//				String expiresInId = getCookieValue(EXPIRES_ID_NAME, request);
//				String issuedAtId = getCookieValue(ISSUED_ID_NAME, request);
//				
//				oidcUserService.loadUser(new OidcUserRequest(clientRegistrationRepository.findByRegistrationId(registrationId),
//						new OAuth2AccessToken(TokenType.BEARER, accessToken,
//								Instant.ofEpochSecond(Long.valueOf(issuedAt)),
//								Instant.ofEpochSecond(Long.valueOf(expiresIn))), new OidcIdToken(idToken, null, null, null), additionalParameters);
//			}
			OAuth2AuthenticationToken authenticationToken = new OAuth2AuthenticationToken(oauth2User,
					Collections.emptyList(), registrationId);

			SecurityContext context = securityContextHolderStrategy.createEmptyContext();
			context.setAuthentication(authenticationToken);

			return context;
		}
		return null;
	}

	@Override
	public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
		if (context.getAuthentication() instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2AuthenticationToken = (OAuth2AuthenticationToken) context
					.getAuthentication();
			OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
					oauth2AuthenticationToken.getAuthorizedClientRegistrationId(), oauth2AuthenticationToken.getName());

			OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

			int expires = (int) accessToken.getExpiresAt().getEpochSecond();
			int maxAge = (int) Instant.ofEpochSecond(expires).minusSeconds(Instant.now().getEpochSecond())
					.getEpochSecond();

			Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_NAME, accessToken.getTokenValue());
			accessTokenCookie.setMaxAge(maxAge);
			accessTokenCookie.setPath("/");

			Cookie expiresInCookie = new Cookie(EXPIRES_IN_NAME, String.valueOf(expires));
			expiresInCookie.setMaxAge(maxAge);
			expiresInCookie.setPath("/");

			Cookie issuedAtCookie = new Cookie(ISSUED_AT_NAME,
					String.valueOf(accessToken.getIssuedAt().getEpochSecond()));
			issuedAtCookie.setMaxAge(maxAge);
			issuedAtCookie.setPath("/");

			Cookie registrationIdCookie = new Cookie(REGISTRATION_ID_NAME,
					authorizedClient.getClientRegistration().getRegistrationId());
			registrationIdCookie.setMaxAge(maxAge);
			registrationIdCookie.setPath("/");

//			if (oauth2AuthenticationToken.getPrincipal() instanceof OidcUser) {
//				OidcIdToken idToken = ((OidcUser) oauth2AuthenticationToken.getPrincipal()).getIdToken();
//
//				int expiresIdToken = (int) idToken.getExpiresAt().getEpochSecond();
//				int maxAgeIdToken = (int) Instant.ofEpochSecond(expiresIdToken)
//						.minusSeconds(Instant.now().getEpochSecond()).getEpochSecond();
//
//				Cookie idTokenCookie = new Cookie(ID_TOKEN_NAME, idToken.getTokenValue());
//				accessTokenCookie.setMaxAge(maxAgeIdToken);
//				accessTokenCookie.setPath("/");
//
//				Cookie expiresInIdCookie = new Cookie(EXPIRES_ID_NAME, String.valueOf(expiresIdToken));
//				expiresInCookie.setMaxAge(maxAgeIdToken);
//				expiresInCookie.setPath("/");
//
//				Cookie issuedAtIdCookie = new Cookie(ISSUED_AT_NAME,
//						String.valueOf(accessToken.getIssuedAt().getEpochSecond()));
//				issuedAtCookie.setMaxAge(maxAgeIdToken);
//				issuedAtCookie.setPath("/");
//
//				response.addCookie(idTokenCookie);
//				response.addCookie(expiresInIdCookie);
//				response.addCookie(issuedAtIdCookie);
//			}

			response.addCookie(accessTokenCookie);
			response.addCookie(expiresInCookie);
			response.addCookie(issuedAtCookie);
			response.addCookie(registrationIdCookie);
		}
	}

	@Override
	public boolean containsContext(HttpServletRequest request) {
		String registrationId = getCookieValue(REGISTRATION_ID_NAME, request);
		String accessToken = getCookieValue(ACCESS_TOKEN_NAME, request);
		String expiresIn = getCookieValue(EXPIRES_IN_NAME, request);
		String issuedAt = getCookieValue(ISSUED_AT_NAME, request);

		return registrationId != null && accessToken != null && expiresIn != null && issuedAt != null;
	}

	private String getCookieValue(String name, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals(name)) {
				return cookies[i].getValue();
			}
		}
		return null;
	}
}
