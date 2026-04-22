package mg.hei.agrifed.agrifedapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateMemberDto extends MemberInformationDto {
    private String collectivityIdentifier;
    private List<SponsorDto> referees;
    private Boolean registrationFeePaid;
    private Boolean membershipDuesPaid;
}