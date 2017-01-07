package exceptions;

public class DataUnsupervisedException extends RuntimeException {

    public DataUnsupervisedException() {
    }

    public DataUnsupervisedException(String message) {
        super(message);
    }

    public DataUnsupervisedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataUnsupervisedException(Throwable cause) {
        super(cause);
    }

    public DataUnsupervisedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
