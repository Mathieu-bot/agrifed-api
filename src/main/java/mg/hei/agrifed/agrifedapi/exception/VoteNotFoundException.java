package mg.hei.agrifed.agrifedapi.exception;

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
