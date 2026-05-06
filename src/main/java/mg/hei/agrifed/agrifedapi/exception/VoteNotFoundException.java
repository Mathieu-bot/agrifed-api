package mg.hei.agrifed.agrifedapi.exception;

class VoteNotFoundException extends ResourceNotFoundException {

    public VoteNotFoundException(String message) {
        super(message);
    }

    public static VoteNotFoundException forMandate(String mandateId) {
        return new VoteNotFoundException(
                "No vote found for the term n°" + mandateId
        );
    }
}
