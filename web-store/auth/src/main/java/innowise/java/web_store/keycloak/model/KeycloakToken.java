package innowise.java.web_store.keycloak.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakToken(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken) {}
