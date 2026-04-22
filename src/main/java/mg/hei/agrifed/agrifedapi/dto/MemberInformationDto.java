package mg.hei.agrifed.agrifedapi.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Base DTO for member information.
 * Used as a base for CreateMember and Member DTOs.
 */
@Getter
@Setter
public class MemberInformationDto {
    private String firstName;
    private String lastName;
    private String birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private int phoneNumber;
    private String email;
    private MemberOccupation occupation;
}