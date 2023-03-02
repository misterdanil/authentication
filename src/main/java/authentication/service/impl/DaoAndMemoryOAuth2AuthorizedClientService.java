package authentication.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;

import com.bebracore.cabinet.model.User;
import com.bebracore.cabinet.service.UserService;

@Service
@Primary
public class DaoAndMemoryOAuth2AuthorizedClientService implements OAuth2AuthorizedClientService {
	@Autowired
	private UserService userService;
	@Autowired
	private InMemoryOAuth2AuthorizedClientService inMemoryOAuth2AuthorizedClientService;

	@Override
	public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId,
			String principalName) {
		return inMemoryOAuth2AuthorizedClientService.loadAuthorizedClient(clientRegistrationId, principalName);
	}

	@Override
	public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
		User user = userService.findByoauth2IdAndOauth2Resource(authorizedClient.getPrincipalName(),
				authorizedClient.getClientRegistration().getRegistrationId());
		if (user == null) {
			user = new User();
			Map<String, Object> attributes = ((OAuth2AuthenticationToken) principal).getPrincipal().getAttributes();

			if (authorizedClient.getClientRegistration().getRegistrationId().equals("vk")) {

				user.setFirstName((String) attributes.get("first_name"));
				user.setLastName((String) attributes.get("last_name"));
				user.setOauth2Id(String.valueOf((Integer) attributes.get("id")));
				user.setEnabled(true);
				user.setOauth2Resource("vk");
			} else if (authorizedClient.getClientRegistration().getRegistrationId().equals("google")) {
				user.setFirstName((String) attributes.get("given_name"));
				user.setLastName((String) attributes.get("family_name"));
				user.setOauth2Id((String) attributes.get("sub"));
				user.setEnabled(true);
				user.setOauth2Resource("google");
			}

			userService.save(user);
		}
		inMemoryOAuth2AuthorizedClientService.saveAuthorizedClient(authorizedClient, principal);

	}

	@Override
	public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
		userService.removeByoauth2IdAndOauth2Resource(principalName, clientRegistrationId);
	}

	public InMemoryOAuth2AuthorizedClientService getInMemoryOAuth2AuthorizedClientService() {
		return inMemoryOAuth2AuthorizedClientService;
	}

}
