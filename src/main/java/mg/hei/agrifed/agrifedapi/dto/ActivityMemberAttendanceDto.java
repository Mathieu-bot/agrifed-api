package mg.hei.agrifed.agrifedapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityMemberAttendanceDto {
    private String id;
    private MemberDescriptionDto memberDescription;
    private String attendanceStatus;
}
