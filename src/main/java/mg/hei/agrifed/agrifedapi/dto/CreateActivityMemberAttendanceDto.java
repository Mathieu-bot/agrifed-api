package mg.hei.agrifed.agrifedapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateActivityMemberAttendanceDto {
    private String memberIdentifier;
    private String attendanceStatus;
}
