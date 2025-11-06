package innowise.java.web_store.keycloak.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import innowise.java.web_store.exception.ApiException;
import innowise.java.web_store.exception.ApiExceptionType;
import innowise.java.web_store.keycloak.constants.ExceptionConstants;
import innowise.java.web_store.keycloak.constants.KeycloakConstants;
import innowise.java.web_store.keycloak.model.KeycloakToken;
import innowise.java.web_store.keycloak.service.KeycloakService;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloak;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Value("${keycloak.client-id}")
    private String userClientId;

    @Value("${keycloak.client-secret}")
    private String userClientSecret;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    private String accessToken;
    private Instant tokenExpiration;

    @Override
    public KeycloakToken signIn(String username, String password) {
        HttpEntity<MultiValueMap<String, String>> requestEntity = createTokenRequestEntity(
                KeycloakConstants.GRANT_TYPE_PASSWORD,
                username,
                password
        );

        ResponseEntity<String> response;
        try {
            response = getResponseWithToken(requestEntity);
        } catch (ResourceAccessException e) {
            throw new ApiException(ApiExceptionType.ERR_REQUEST_TIMEOUT, ExceptionConstants.KEYCLOAK_UNAVAILABLE);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ApiException(ApiExceptionType.ERR_AUTH_INVALID, ExceptionConstants.INVALID_CREDENTIALS);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ApiException(ApiExceptionType.ERR_RECORD_NOT_FOUND, ExceptionConstants.USER_NOT_FOUND);
        } catch (HttpClientErrorException e) {
            throw new ApiException(ApiExceptionType.ERR_KEYCLOAK,
                    ExceptionConstants.KEYCLOAK_CLIENT_ERROR);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionType.ERR_INTERNAL_ERROR, ExceptionConstants.AUTH_ERROR);
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ApiException(ApiExceptionType.ERR_KEYCLOAK,
                    String.format(ExceptionConstants.KEYCLOAK_NO_TOKEN, response.getStatusCode()));
        }

        try {
            return objectMapper.readValue(response.getBody(), KeycloakToken.class);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionType.ERR_KEYCLOAK, ExceptionConstants.TOKEN_PARSE_ERROR);
        }
    }

    @Override
    public KeycloakToken refresh(String refreshToken) {
        HttpEntity<MultiValueMap<String, String>> request = createTokenRequestEntity(
                KeycloakConstants.GRANT_TYPE_REFRESH_TOKEN, refreshToken);

        ResponseEntity<String> response;
        try {
            response = getResponseWithToken(request);
        } catch (ResourceAccessException e) {
            throw new ApiException(ApiExceptionType.ERR_REQUEST_TIMEOUT, ExceptionConstants.KEYCLOAK_UNAVAILABLE);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new ApiException(ApiExceptionType.ERR_AUTH_INVALID, ExceptionConstants.INVALID_CREDENTIALS);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ApiException(ApiExceptionType.ERR_RECORD_NOT_FOUND, ExceptionConstants.USER_NOT_FOUND);
        } catch (HttpClientErrorException e) {
            throw new ApiException(ApiExceptionType.ERR_KEYCLOAK,
                    ExceptionConstants.KEYCLOAK_CLIENT_ERROR);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionType.ERR_INTERNAL_ERROR, ExceptionConstants.AUTH_ERROR);
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ApiException(ApiExceptionType.ERR_KEYCLOAK,
                    String.format(ExceptionConstants.KEYCLOAK_NO_TOKEN, response.getStatusCode()));
        }

        try {
            return objectMapper.readValue(response.getBody(), KeycloakToken.class);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionType.ERR_KEYCLOAK, ExceptionConstants.TOKEN_PARSE_ERROR);
        }
    }

    @Override
    public void updateUserRoles(String userId, List<String> toDelete, List<String> toAdd) {
        RealmResource realmResource = keycloak.realm(keycloakRealm);
        UserResource userResource = realmResource
                .users().get(userId);

        if (toDelete != null && !toDelete.isEmpty()) {
            List<RoleRepresentation> rolesToRemove = toDelete.stream()
                    .map(roleName -> realmResource.roles().get(roleName).toRepresentation())
                    .toList();
            userResource.roles().realmLevel().remove(rolesToRemove);
        }

        if (toAdd != null && !toAdd.isEmpty()) {
            List<RoleRepresentation> rolesToAdd = toAdd.stream()
                    .map(roleName -> realmResource.roles().get(roleName).toRepresentation())
                    .toList();
            userResource.roles().realmLevel().add(rolesToAdd);
        }
    }

    @Override
    public void deleteUser(String userId) {
        RealmResource realmResource = keycloak.realm(keycloakRealm);
        UsersResource usersResource = realmResource.users();
        try {
            usersResource.delete(userId);
        } catch (Exception ex) {
            throw new ApiException(ApiExceptionType.ERR_KEYCLOAK);
        }
    }

    private HttpEntity<MultiValueMap<String, String>> createTokenRequestEntity(String grantType, String... params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(KeycloakConstants.APPLICATION_FORM_URLENCODED));
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(KeycloakConstants.GRANT_TYPE, grantType);

        switch (grantType) {
            case KeycloakConstants.GRANT_TYPE_PASSWORD: {
                body.add(KeycloakConstants.USERNAME, params[0]);
                body.add(KeycloakConstants.PASSWORD, params[1]);
                body.add(KeycloakConstants.CLIENT_ID, userClientId);
                body.add(KeycloakConstants.CLIENT_SECRET, userClientSecret);
                break;
            }
            case KeycloakConstants.GRANT_TYPE_REFRESH_TOKEN: {
                body.add(KeycloakConstants.REFRESH_TOKEN, params[0]);
                body.add(KeycloakConstants.CLIENT_ID, userClientId);
                body.add(KeycloakConstants.CLIENT_SECRET, userClientSecret);
                break;
            }
        }

        return new HttpEntity<>(body, headers);
    }

    private ResponseEntity<String> getResponseWithToken(HttpEntity<MultiValueMap<String, String>> requestEntity) {
        try {
            return restTemplate.exchange(getAuthUrl(), HttpMethod.POST, requestEntity, String.class);
        } catch (HttpClientErrorException | ResourceAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(ApiExceptionType.ERR_KEYCLOAK, ExceptionConstants.SEND_TOKEN_REQUEST_ERROR);
        }
    }

    private String getAuthUrl() {
        return keycloakServerUrl +
                "/realms/" + keycloakRealm +
                "/protocol/openid-connect/token";
    }

    @Override
    public String getAccessToken() {
        if (accessToken != null && tokenExpiration != null && Instant.now().isBefore(tokenExpiration)) {
            return accessToken;
        }

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(KeycloakConstants.GRANT_TYPE, "client_credentials");
        formData.add(KeycloakConstants.CLIENT_ID, userClientId);
        formData.add(KeycloakConstants.CLIENT_SECRET, userClientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    getAuthUrl(),
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode json = objectMapper.readTree(response.getBody());
                accessToken = json.get("access_token").asText();

                long expiresIn = json.get("expires_in").asLong();
                tokenExpiration = Instant.now().plusSeconds(expiresIn - 30); // небольшой запас

                return accessToken;
            } else {
                throw new ApiException(ApiExceptionType.ERR_KEYCLOAK,
                        String.format(ExceptionConstants.KEYCLOAK_NO_TOKEN, response.getStatusCode()));
            }

        } catch (Exception e) {
            throw new ApiException(ApiExceptionType.ERR_KEYCLOAK);
        }
    }

    @Override
    public String createUser(String email, String password, Map<String, List<String>> attributes) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);

        UserRepresentation keycloakUser = new UserRepresentation();
        keycloakUser.setEmail(email);
        keycloakUser.setCredentials(List.of(credential));
        keycloakUser.setEnabled(true);
        keycloakUser.setAttributes(attributes);

        RealmResource realmResource = keycloak.realm(keycloakRealm);
        UsersResource usersResource = realmResource.users();

        Response response;
        try {
            response = Optional.ofNullable(usersResource.create(keycloakUser))
                    .orElseThrow(() -> new ApiException(ApiExceptionType.ERR_KEYCLOAK, ExceptionConstants.KEYCLOAK_USER_CREATION_FAILED));
        } catch (Exception e) {
            throw new ApiException(ApiExceptionType.ERR_KEYCLOAK, ExceptionConstants.KEYCLOAK_USER_CREATE_EXCEPTION);
        }

        if (response.getStatus() != HttpStatus.CREATED.value()) {
            String reason =
                    response.getStatusInfo() != null ? response.getStatusInfo().toString() : "неизвестная ошибка";
            throw new ApiException(ApiExceptionType.ERR_KEYCLOAK, String.format(ExceptionConstants.KEYCLOAK_USER_NOT_CREATED, reason));
        }

        String keycloakUserId;
        try {
            keycloakUserId = CreatedResponseUtil.getCreatedId(response);
        } catch (Exception e) {
            throw new ApiException(ApiExceptionType.ERR_KEYCLOAK, ExceptionConstants.KEYCLOAK_GET_USER_ID_FAILED);
        }

        return keycloakUserId;
    }
}
