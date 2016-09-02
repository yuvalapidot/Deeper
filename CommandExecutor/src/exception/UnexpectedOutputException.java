package exception;

/**
 * Created by yuval on 9/3/2016.
 */
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
