package mg.hei.agrifed.agrifedapi.exception;

/**
 * Custom exception for database-related errors.
 * Wraps SQL exceptions with a meaningful message.
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
