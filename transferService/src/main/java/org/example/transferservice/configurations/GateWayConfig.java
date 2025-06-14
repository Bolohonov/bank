package org.example.transferservice.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.client.RestClient;

@Configuration
public class GateWayConfig {

    @Value("${bank.gateway}")
    private String gatewayUrl;

    @Value("${spring.security.oauth2.client.registration.service-client.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.service-client.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUri;

    static private String ACCOUNT_SERVICE = "accountservice";

    static private String BLOCKER_SERVICE = "blockerservice";

    static private String NOTIFICATIONS_SERVICE = "notificationsservice";

    static private String EXCHANGE_SERVICE = "exchangeservice";

    @Bean
    public String notificationsUrl() {
        return gatewayUrl + "/" + NOTIFICATIONS_SERVICE + "/notifications";
    }

    @Bean
    public String accountApplicationUrl() {
        return gatewayUrl + "/" + ACCOUNT_SERVICE;
    }

    @Bean
    public String blockerApplicationUrl() {
        return gatewayUrl + "/" + BLOCKER_SERVICE + "/blocker";
    }

    @Bean
    public String exchangeApplicationUrl() {
        return gatewayUrl + "/" + EXCHANGE_SERVICE +"/exchange";
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        ClientRegistration keycloakClient = ClientRegistration.withRegistrationId("oauth-yabank")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .tokenUri(tokenUri)
                .scope("accounts.read","exchange.read","blocker.read","notifications.write")
                .build();
        return new InMemoryClientRegistrationRepository(keycloakClient);
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService
    ) {
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);

        manager.setAuthorizedClientProvider(OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials() // Для межсервисной аутентификации
                .refreshToken()      // Для обновления токенов
                .build());

        return manager;
    }

    @Bean
    public RestClient restClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        OAuth2ClientHttpRequestInterceptor requestInterceptor =
                new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
        requestInterceptor.setClientRegistrationIdResolver(request -> "oauth-yabank");
        return RestClient.builder()
                .requestInterceptor(requestInterceptor)
                .build();
    }

}
