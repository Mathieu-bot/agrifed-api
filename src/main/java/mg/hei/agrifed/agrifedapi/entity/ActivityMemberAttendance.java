package mg.hei.agrifed.agrifedapi.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityMemberAttendance {
    private String id;
    private String memberId;
    private String activityId;
    private String status;
    private Boolean isExternal;
}
