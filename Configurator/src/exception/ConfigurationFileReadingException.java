package exception;

public class ConfigurationFileReadingException extends RuntimeException {

    public ConfigurationFileReadingException() {
    }

    public ConfigurationFileReadingException(String message) {
        super(message);
    }

    public ConfigurationFileReadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationFileReadingException(Throwable cause) {
        super(cause);
    }

    public ConfigurationFileReadingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
