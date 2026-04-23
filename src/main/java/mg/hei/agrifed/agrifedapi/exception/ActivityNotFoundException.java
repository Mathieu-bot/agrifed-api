package mg.hei.agrifed.agrifedapi.exception;

public class ActivityNotFoundException extends ResourceNotFoundException {

    public ActivityNotFoundException(String message) {
        super(message);
    }
}

