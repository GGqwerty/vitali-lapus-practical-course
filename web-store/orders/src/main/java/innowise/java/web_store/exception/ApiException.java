package innowise.java.web_store.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus httpStatus;

    private final String mainMessage;

    private final String additionalMessage;

    public ApiException(ApiExceptionType e) {
        super(e.name());
        httpStatus = e.getHttpStatus();
        mainMessage = e.getDisplayMessage();
        additionalMessage = "";
    }

    public ApiException(ApiExceptionType e, String additionalMessage) {
        super(e.name());
        httpStatus = e.getHttpStatus();
        mainMessage = e.getDisplayMessage();
        this.additionalMessage = additionalMessage;
    }
}
