package mg.hei.agrifed.agrifedapi.entity;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityMemberAttendance {
    private String id;
    private String memberId;
    private String activityId;
    private LocalDate occurrenceDate;
    private String status;
    private Boolean isExternal;
}
