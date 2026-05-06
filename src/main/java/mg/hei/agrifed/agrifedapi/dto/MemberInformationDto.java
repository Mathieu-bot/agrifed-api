package mg.hei.agrifed.agrifedapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInformationDto {
    private String firstName;
    private String lastName;
    private String birthDate;
    private Gender gender;
    private String address;
    private String profession;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String phoneNumber;
    private String email;
    private MemberOccupation occupation;
}