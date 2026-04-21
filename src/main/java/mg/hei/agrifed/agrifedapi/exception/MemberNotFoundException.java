package mg.hei.agrifed.agrifedapi.exception;

public class MemberNotFoundException extends ResourceNotFoundException {
    
    public MemberNotFoundException(String message) {
        super(message);
    }
    
    public MemberNotFoundException(int id) {
        super("Member with ID " + id + " is not found in the federation.");
    }
}
