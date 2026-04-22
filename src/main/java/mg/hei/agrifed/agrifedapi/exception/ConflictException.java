package mg.hei.agrifed.agrifedapi.exception;

public class ConflictException extends RuntimeException {
    
    public ConflictException(String message) {
        super(message);
    }
    
    public static ConflictException emailAlreadyExists(String email) {
        return new ConflictException(
            "A member with email address '" + email + "' already exists in the federation. " +
            "Please use another email address."
        );
    }
    
    public static ConflictException phoneAlreadyExists(String phone) {
        return new ConflictException(
            "A member with the phone number '" + phone + "' already exists in the federation. " +
            "Please use another phone number"
        );
    }
    
    public static ConflictException collectivityNameAlreadyExists(String name) {
        return new ConflictException(
            "A collectivity with the name '" + name + "' already exists in the federation. " +
            "Please use another collectivity name"
        );
    }
    
    public static ConflictException collectivityNumberAlreadyExists(long numero) {
        return new ConflictException(
            "Collectivity n° " + numero + " is already attributed. " +
            "Please use another number"
        );
    }
}
