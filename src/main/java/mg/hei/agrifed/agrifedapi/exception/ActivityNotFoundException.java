package mg.hei.agrifed.agrifedapi.exception;

public class ActivityNotFoundException extends ResourceNotFoundException {
    
    public ActivityNotFoundException(String message) {
        super(message);
    }
    
    public ActivityNotFoundException(int formationId) {
        super("Formation with ID " + formationId + " is not found");
    }
}

