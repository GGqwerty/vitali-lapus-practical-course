package innowise.java.web_store.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ApiExceptionType {

    ERR_BAD_REQUEST( "Недопустимый формат запроса", HttpStatus.BAD_REQUEST),

    ERR_AUTH_INVALID("Неверный логин или пароль", HttpStatus.UNAUTHORIZED),

    ERR_NO_RIGHTS( "Нет прав на выполнение операции", HttpStatus.FORBIDDEN),

    ERR_NOT_FOUND( "Не найдено", HttpStatus.NOT_FOUND),

    ERR_NOT_ALLOWED("Метод недоступен", HttpStatus.METHOD_NOT_ALLOWED),

    ERR_REQUEST_TIMEOUT("Истекло время ожидания", HttpStatus.REQUEST_TIMEOUT),

    ERR_KEYCLOAK("Ошибка Keycloak", HttpStatus.INTERNAL_SERVER_ERROR),

    ERR_RECORD_NOT_FOUND("Объект не найден", HttpStatus.NOT_FOUND),

    ERR_INTERNAL_ERROR("Техническая ошибка", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String displayMessage;
    private final HttpStatus httpStatus;

}
