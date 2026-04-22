package mg.hei.agrifed.agrifedapi.exception;

public class MandateNotFoundException extends ResourceNotFoundException {
    
    public MandateNotFoundException(String message) {
        super(message);
    }
    
    public MandateNotFoundException(int mandateId) {
        super("The term with ID " + mandateId + " is not found");
    }

}
