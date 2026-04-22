package mg.hei.agrifed.agrifedapi.exception;

public class UnauthorizedException extends RuntimeException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
    
    public static UnauthorizedException missingToken() {
        return new UnauthorizedException(
            "Missing authentication token. Please log in again."
        );
    }
    
    public static UnauthorizedException invalidToken() {
        return new UnauthorizedException(
            "Invalid or expired authentication token. Please log in again."
        );
    }
    
    public static UnauthorizedException sessionExpired() {
        return new UnauthorizedException(
            "Session expired. Please log in."
        );
    }
}
