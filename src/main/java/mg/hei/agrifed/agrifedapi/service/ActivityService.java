package mg.hei.agrifed.agrifedapi.service;

import mg.hei.agrifed.agrifedapi.dto.*;

import java.util.List;

public interface ActivityService {
    List<CollectivityActivityDto> getActivities(String collectivityId);
    List<CollectivityActivityDto> createActivities(String collectivityId, List<CreateCollectivityActivityDto> dtos);
    List<ActivityMemberAttendanceDto> confirmAttendance(String collectivityId, String activityId, List<CreateActivityMemberAttendanceDto> dtos);
    List<ActivityMemberAttendanceDto> getAttendance(String collectivityId, String activityId);
}
