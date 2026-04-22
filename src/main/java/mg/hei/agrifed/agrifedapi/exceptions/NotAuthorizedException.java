package mg.hei.agrifed.agrifedapi.exceptions;

public class NotAuthorizedException extends RuntimeException {
    
    public NotAuthorizedException(String message) {
        super(message);
    }
    
    public static NotAuthorizedException insufficientPermissions(String action) {
        return new NotAuthorizedException(
            "You do not have the necessary permissions to perform this action : " + action
        );
    }

    public static NotAuthorizedException onlyConfirmedMembersCanSponsor() {
        return new NotAuthorizedException(
            "Only confirmed members can sponsor a new member"
        );
    }
    
    public static NotAuthorizedException onlySecretaryAllowed() {
        return new NotAuthorizedException(
            "This action is reserved for the secretary of the collectivity or federation"
        );
    }
    
    public static NotAuthorizedException cannotModifyOtherMemberData() {
        return new NotAuthorizedException(
            "You cannot edit another member's information"
        );
    }
    
    public static NotAuthorizedException juniorMemberRestricted(String feature) {
        return new NotAuthorizedException(
            "Junior members don't have access to : " + feature + ". "
        );
    }
}
