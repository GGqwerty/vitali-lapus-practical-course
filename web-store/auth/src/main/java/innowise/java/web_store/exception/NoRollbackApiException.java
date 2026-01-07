package innowise.java.web_store.exception;

public class NoRollbackApiException extends RuntimeException {

    public NoRollbackApiException(String message) {
        super(message);
    }
}
