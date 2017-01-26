package exceptions;

public class MulticlassNotSupportedException extends RuntimeException {

    public MulticlassNotSupportedException() {
    }

    public MulticlassNotSupportedException(String message) {
        super(message);
    }

    public MulticlassNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public MulticlassNotSupportedException(Throwable cause) {
        super(cause);
    }

    public MulticlassNotSupportedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
