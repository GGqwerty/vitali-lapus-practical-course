package innowise.java.web_store.service.impl;

import innowise.java.web_store.dto.request.SignInRequest;
import innowise.java.web_store.dto.request.SignUpRequest;
import innowise.java.web_store.dto.response.TokenResponse;
import innowise.java.web_store.exception.ApiException;
import innowise.java.web_store.exception.ApiExceptionType;
import innowise.java.web_store.exception.NoRollbackApiException;
import innowise.java.web_store.keycloak.constants.ExceptionConstants;
import innowise.java.web_store.keycloak.model.KeycloakToken;
import innowise.java.web_store.keycloak.service.KeycloakService;
import innowise.java.web_store.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KeycloakService keycloakService;

    @Value("${services.user-service-url}")
    private String userServiceUrl;

    @Override
    public TokenResponse signIn(SignInRequest signInRequest) {
        KeycloakToken token = keycloakService.signIn(signInRequest.getUsername(), signInRequest.getPassword());

        return new TokenResponse(token.accessToken(), token.refreshToken());
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        KeycloakToken token = keycloakService.refresh(refreshToken);

        return new TokenResponse(token.accessToken(), token.refreshToken());
    }

    @Override
    public TokenResponse signUp(SignUpRequest signUp) {
        Map<String, List<String>> attributes = new HashMap<>();

        String keycloakUserId = keycloakService.createUser(signUp.getEmail(),
                signUp.getPassword(), attributes);

        try {
            keycloakService.updateUserRoles(keycloakUserId, List.of(), List.of(signUp.getRole()));
        } catch (Exception e) {
            try {
                keycloakService.deleteUser(keycloakUserId);
            } catch (Exception ignored) {
            }
            throw new ApiException(ApiExceptionType.ERR_KEYCLOAK,
                    String.format(ExceptionConstants.ASSIGN_ROLE_FAILED, signUp.getRole()));
        }

        try {
            return signIn(new SignInRequest(signUp.getEmail(), signUp.getPassword()));
        } catch (Exception ignored) {
            throw new NoRollbackApiException("Registered, but not auth");
        }
    }

    @Override
    public void delete(String email) {
        keycloakService.deleteByEmail(email);
    }
}