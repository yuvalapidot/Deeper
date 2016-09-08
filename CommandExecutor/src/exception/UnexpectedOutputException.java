package exception;

public class UnexpectedOutputException extends RuntimeException{

    public UnexpectedOutputException() {
    }

    public UnexpectedOutputException(String message) {
        super(message);
    }

    public UnexpectedOutputException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedOutputException(Throwable cause) {
        super(cause);
    }

    public UnexpectedOutputException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
