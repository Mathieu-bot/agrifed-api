package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.ActivityMemberAttendance;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository {
    List<ActivityMemberAttendance> findAllByActivityId(String activityId);
    Optional<ActivityMemberAttendance> findByActivityIdAndMemberId(String activityId, String memberId);
    List<ActivityMemberAttendance> findAllByActivityIdAndIsExternal(String activityId, boolean isExternal);
    ActivityMemberAttendance save(ActivityMemberAttendance attendance);
    List<ActivityMemberAttendance> saveAll(List<ActivityMemberAttendance> attendances);
    boolean existsByActivityIdAndMemberId(String activityId, String memberId);
}
