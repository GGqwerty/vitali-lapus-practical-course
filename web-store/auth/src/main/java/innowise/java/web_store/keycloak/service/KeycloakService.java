package innowise.java.web_store.keycloak.service;

import innowise.java.web_store.keycloak.model.KeycloakToken;

import java.util.List;
import java.util.Map;

public interface KeycloakService {

    String createUser(String email, String password, Map<String, List<String>> attributes);

    void deleteUser(String userId);

    void deleteByEmail(String email);

    void updateUserRoles(String userId, List<String> toDelete, List<String> toAdd);

    KeycloakToken refresh(String refreshToken);

    KeycloakToken signIn(String username, String password);

    String getAccessToken();
}
