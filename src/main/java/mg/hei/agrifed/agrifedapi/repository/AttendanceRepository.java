package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.ActivityMemberAttendance;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository {
    List<ActivityMemberAttendance> findAllByActivityId(String activityId);
    Optional<ActivityMemberAttendance> findByActivityIdAndMemberId(String activityId, String memberId);
    List<ActivityMemberAttendance> findAllByActivityIdAndIsExternal(String activityId, boolean isExternal);
    List<ActivityMemberAttendance> findAllByCollectivityId(String collectivityId);
    ActivityMemberAttendance save(ActivityMemberAttendance attendance);
    List<ActivityMemberAttendance> saveAll(List<ActivityMemberAttendance> attendances);
    boolean existsByActivityIdAndMemberIdAndStatusIn(String activityId, String memberId, List<String> statuses);
}
