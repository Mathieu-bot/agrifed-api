package mg.hei.agrifed.agrifedapi.exception;

public class ActivityNotFoundException extends ResourceNotFoundException {
    
    public ActivityNotFoundException(String message) {
        super(message);
    }
    
    public ActivityNotFoundException(int formationId) {
        super("Formation with ID " + formationId + " is not found");
    }
}

class VoteNotFoundException extends ResourceNotFoundException {
    
    public VoteNotFoundException(String message) {
        super(message);
    }
    
    public VoteNotFoundException(int voteId) {
        super("Vote with ID " + voteId + " is not found");
    }
    
    public static VoteNotFoundException forMandate(int mandateId) {
        return new VoteNotFoundException(
            "No vote found for the term n°" + mandateId
        );
    }
}
