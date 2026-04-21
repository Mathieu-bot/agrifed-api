package mg.hei.agrifed.agrifedapi.exception;

import lombok.Getter;

@Getter
public class BusinessRuleViolationException extends RuntimeException {
    private final String rule;

    public BusinessRuleViolationException(String message, String rule) {
        super(message);
        this.rule = rule;
    }

    public static BusinessRuleViolationException insufficientSponsors(int actualCount) {
        return new BusinessRuleViolationException(
                "The candidate should be sponsored by at least 2 confirmed members. " +
                        "Actual sponsor count : " + actualCount,
                "INSUFFICIENT_SPONSORS"
        );
    }

    public static BusinessRuleViolationException sponsorNotConfirmedMember(String memberName) {
        return new BusinessRuleViolationException(
                "The sponsor '" + memberName + "' isn't a confirmed member. " +
                        "Only confirmed members can sponsor a new candidate",
                "SPONSOR_NOT_CONFIRMED"
        );
    }

    public static BusinessRuleViolationException unpaidMembershipFee() {
        return new BusinessRuleViolationException(
                "The membership fee of 50,000 Ar must be paid to complete the registration",
                "UNPAID_MEMBERSHIP_FEE"
        );
    }

    public static BusinessRuleViolationException unpaidAnnualContribution(
            long collectivityId,
            double requiredAmount) {
        return new BusinessRuleViolationException(
                String.format(
                        "The annual contribution of %,.0f Ar imposed by collectivity n°%d " + "must be paid in full",
                        requiredAmount, collectivityId
                ),
                "UNPAID_ANNUAL_CONTRIBUTION"
        );
    }

    public static BusinessRuleViolationException memberNotUpToDate(int memberId, String memberName) {
        return new BusinessRuleViolationException(
                "The member " + memberName + " with ID " + memberId + " is not up to date " +
                        "with their contributions and cannot perform certain functions",
                "MEMBER_NOT_UP_TO_DATE"
        );
    }

    public static BusinessRuleViolationException maxTermsReached(
            String memberName,
            String position,
            int currentMandateCount) {
        return new BusinessRuleViolationException(
                String.format(
                        "The member '%s' has already held the position of '%s' %d times. " +
                                "he maximum limit of 2 terms per position has been reached",
                        memberName, position, currentMandateCount
                ),
                "MAX_TERMS_REACHED"
        );
    }

    public static BusinessRuleViolationException uniquePositionAlreadyFilled(
            String position,
            String currentHolder,
            int mandateYear) {
        return new BusinessRuleViolationException(
                String.format(
                        "The position of '%s' for the term %d is already occupied by %s. " +
                                "The positions of president, vice president, treasurer, and secretary " +
                                "may be held by only one member per term",
                        position, mandateYear, currentHolder
                ),
                "UNIQUE_POSITION_ALREADY_FILLED"
        );
    }

    public static BusinessRuleViolationException federationPresidentNotFormerCollectivityPresident(
            String memberName) {
        return new BusinessRuleViolationException(
                "The member '" + memberName + "' can't be president of the federation " +
                        "because he/she's never been a collectivity president. " +
                        "This position is only reserved to formers or actual collectivity presidents.",
                "INVALID_FEDERATION_PRESIDENT_CANDIDATE"
        );
    }
}
