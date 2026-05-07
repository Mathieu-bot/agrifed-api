package mg.hei.agrifed.agrifedapi.controller;

import mg.hei.agrifed.agrifedapi.dto.*;
import mg.hei.agrifed.agrifedapi.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities/{id}/activities")
public class ActivityRestController {

    private final ActivityService activityService;

    public ActivityRestController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<List<CollectivityActivityDto>> getActivities(@PathVariable String id) {
        return ResponseEntity.ok(activityService.getActivities(id));
    }

    @PostMapping
    public ResponseEntity<List<CollectivityActivityDto>> createActivities(
            @PathVariable String id,
            @RequestBody List<CreateCollectivityActivityDto> activities) {
        return ResponseEntity.ok(activityService.createActivities(id, activities));
    }

    @PostMapping("/{activityId}/attendance")
    public ResponseEntity<List<ActivityMemberAttendanceDto>> confirmAttendance(
            @PathVariable String id,
            @PathVariable String activityId,
            @RequestBody List<CreateActivityMemberAttendanceDto> attendances) {
        return ResponseEntity.status(201).body(activityService.confirmAttendance(id, activityId, attendances));
    }

    @GetMapping("/{activityId}/attendance")
    public ResponseEntity<List<ActivityMemberAttendanceDto>> getAttendance(
            @PathVariable String id,
            @PathVariable String activityId) {
        return ResponseEntity.ok(activityService.getAttendance(id, activityId));
    }
}
