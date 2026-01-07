package innowise.java.web_store.keycloak.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KeycloakConstants {
    public static final String GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String REFRESH_TOKEN = "refresh_token";

}
