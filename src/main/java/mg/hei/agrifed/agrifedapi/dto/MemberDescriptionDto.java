package mg.hei.agrifed.agrifedapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDescriptionDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String occupation;
}