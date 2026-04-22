package mg.hei.agrifed.agrifedapi.entity;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    private Integer id;
    private String lastName;
    private String firstName;
    private LocalDate birthDate;
    private GenderEnum gender;
    private String address;
    private String occupation;
    private String phone;
    private String email;
    private LocalDate membershipDate;
    private Boolean registrationFeePaid;
    private Boolean membershipDuesPaid;
}
