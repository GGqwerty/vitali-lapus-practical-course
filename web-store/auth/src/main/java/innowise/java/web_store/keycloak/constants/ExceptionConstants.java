package innowise.java.web_store.keycloak.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExceptionConstants {

    public static final String KEYCLOAK_USER_CREATION_FAILED = "Ошибка Keycloak: не удалось создать пользователя";
    public static final String KEYCLOAK_USER_CREATE_EXCEPTION = "Ошибка при создании пользователя в Keycloak";
    public static final String KEYCLOAK_USER_NOT_CREATED = "Keycloak не создал пользователя: %s";
    public static final String KEYCLOAK_GET_USER_ID_FAILED = "Не удалось получить ID пользователя после создания";
    public static final String KEYCLOAK_UNAVAILABLE = "Keycloak недоступен";
    public static final String INVALID_CREDENTIALS = "Неправильный логин или пароль";
    public static final String USER_NOT_FOUND = "Пользователь не найден";
    public static final String KEYCLOAK_CLIENT_ERROR = "Ошибка Keycloak: %s";
    public static final String AUTH_ERROR = "Ошибка авторизации";
    public static final String KEYCLOAK_NO_TOKEN = "Keycloak не вернул токен: %s";
    public static final String TOKEN_PARSE_ERROR = "Ошибка при обработке токена авторизации";
    public static final String SEND_TOKEN_REQUEST_ERROR = "Ошибка отправки запроса на получение токена";
    public static final String ASSIGN_ROLE_FAILED = "Ошибка при назначении роли пользователю: %s";
}
