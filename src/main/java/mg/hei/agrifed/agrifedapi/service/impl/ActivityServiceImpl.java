package mg.hei.agrifed.agrifedapi.service.impl;

import mg.hei.agrifed.agrifedapi.dto.*;
import mg.hei.agrifed.agrifedapi.entity.ActivityMemberAttendance;
import mg.hei.agrifed.agrifedapi.entity.CollectivityActivity;
import mg.hei.agrifed.agrifedapi.entity.Member;
import mg.hei.agrifed.agrifedapi.exception.ActivityNotFoundException;
import mg.hei.agrifed.agrifedapi.exception.BadRequestException;
import mg.hei.agrifed.agrifedapi.exception.CollectivityNotFoundException;
import mg.hei.agrifed.agrifedapi.repository.ActivityRepository;
import mg.hei.agrifed.agrifedapi.repository.AttendanceRepository;
import mg.hei.agrifed.agrifedapi.repository.CollectivityRepository;
import mg.hei.agrifed.agrifedapi.repository.MemberRepository;
import mg.hei.agrifed.agrifedapi.service.ActivityService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;
    private final CollectivityRepository collectivityRepository;

    public ActivityServiceImpl(
            ActivityRepository activityRepository,
            AttendanceRepository attendanceRepository,
            MemberRepository memberRepository,
            CollectivityRepository collectivityRepository) {
        this.activityRepository = activityRepository;
        this.attendanceRepository = attendanceRepository;
        this.memberRepository = memberRepository;
        this.collectivityRepository = collectivityRepository;
    }

    @Override
    public List<CollectivityActivityDto> getActivities(String collectivityId) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new CollectivityNotFoundException("Collectivity not found: " + collectivityId));

        List<CollectivityActivity> activities = activityRepository.findAllByCollectivityId(collectivityId);
        return activities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<CollectivityActivityDto> createActivities(String collectivityId, List<CreateCollectivityActivityDto> dtos) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new CollectivityNotFoundException("Collectivity not found: " + collectivityId));

        List<CollectivityActivityDto> result = new ArrayList<>();
        for (CreateCollectivityActivityDto dto : dtos) {
            validateActivity(dto);
            CollectivityActivity activity = toEntity(dto, collectivityId);
            activityRepository.save(activity);
            if (dto.getMemberOccupationConcerned() != null && !dto.getMemberOccupationConcerned().isEmpty()) {
                activityRepository.saveOccupations(activity.getId(), dto.getMemberOccupationConcerned());
            }
            result.add(toDto(activity));
        }
        return result;
    }

    @Override
    public List<ActivityMemberAttendanceDto> confirmAttendance(String collectivityId, String activityId, List<CreateActivityMemberAttendanceDto> dtos) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new CollectivityNotFoundException("Collectivity not found: " + collectivityId));

        CollectivityActivity activity = activityRepository.findByIdAndCollectivityId(activityId, collectivityId)
                .orElseThrow(() -> new ActivityNotFoundException("Activity not found: " + activityId));

        LocalDate occurrenceDate = deriveOccurrenceDate(activity);

        List<ActivityMemberAttendanceDto> result = new ArrayList<>();
        for (CreateActivityMemberAttendanceDto dto : dtos) {
            if (dto.getMemberIdentifier() == null || dto.getMemberIdentifier().isBlank()) {
                throw BadRequestException.missingRequiredField("memberIdentifier");
            }
            if (dto.getAttendanceStatus() == null) {
                throw BadRequestException.missingRequiredField("attendanceStatus");
            }

            List<String> confirmedStatuses = List.of("ATTENDED", "MISSING");
            boolean alreadyConfirmed = attendanceRepository.existsByActivityIdAndMemberIdAndStatusIn(activityId, dto.getMemberIdentifier(), confirmedStatuses);
            if (alreadyConfirmed) {
                throw new BadRequestException("Attendance already confirmed for member " + dto.getMemberIdentifier());
            }

            Member member = memberRepository.findById(dto.getMemberIdentifier())
                    .orElseThrow(() -> new mg.hei.agrifed.agrifedapi.exception.MemberNotFoundException("Member not found: " + dto.getMemberIdentifier()));

            boolean isExternal = !collectivityId.equals(member.getCollectivityId());

            ActivityMemberAttendance attendance = new ActivityMemberAttendance();
            attendance.setMemberId(member.getId());
            attendance.setActivityId(activityId);
            attendance.setOccurrenceDate(occurrenceDate);
            attendance.setStatus(dto.getAttendanceStatus());
            attendance.setIsExternal(isExternal);

            attendanceRepository.save(attendance);

            result.add(toAttendanceDto(attendance, member));
        }
        return result;
    }

    @Override
    public List<ActivityMemberAttendanceDto> getAttendance(String collectivityId, String activityId) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new CollectivityNotFoundException("Collectivity not found: " + collectivityId));

        CollectivityActivity activity = activityRepository.findByIdAndCollectivityId(activityId, collectivityId)
                .orElseThrow(() -> new ActivityNotFoundException("Activity not found: " + activityId));

        List<String> concernedOccupations = activity.getMemberOccupationConcerned() != null
                ? activity.getMemberOccupationConcerned()
                : Collections.emptyList();

        List<ActivityMemberAttendanceDto> result = new ArrayList<>();
        Set<String> seenMemberIds = new HashSet<>();

        List<ActivityMemberAttendance> allAttendances = attendanceRepository.findAllByActivityId(activityId);
        Map<String, ActivityMemberAttendance> attendanceByMemberId = new HashMap<>();
        for (ActivityMemberAttendance a : allAttendances) {
            attendanceByMemberId.put(a.getMemberId(), a);
        }

        List<Member> activeMembers = memberRepository.findActiveMembersByCollectivityId(collectivityId);

        for (Member member : activeMembers) {
            if (concernedOccupations.isEmpty() || concernedOccupations.contains(member.getOccupation())) {
                ActivityMemberAttendance attendance = attendanceByMemberId.get(member.getId());
                if (attendance != null) {
                    result.add(toAttendanceDto(attendance, member));
                } else {
                    ActivityMemberAttendance undefined = new ActivityMemberAttendance();
                    undefined.setId(null);
                    undefined.setMemberId(member.getId());
                    undefined.setStatus("UNDEFINED");
                    undefined.setIsExternal(false);
                    result.add(toAttendanceDto(undefined, member));
                }
                seenMemberIds.add(member.getId());
            }
        }

        for (ActivityMemberAttendance attendance : allAttendances) {
            if (seenMemberIds.contains(attendance.getMemberId())) {
                continue;
            }
            if ("ATTENDED".equals(attendance.getStatus())) {
                Member member = memberRepository.findById(attendance.getMemberId()).orElse(null);
                if (member != null) {
                    result.add(toAttendanceDto(attendance, member));
                    seenMemberIds.add(attendance.getMemberId());
                }
            }
        }

        return result;
    }

    private LocalDate deriveOccurrenceDate(CollectivityActivity activity) {
        if (activity.getExecutiveDate() != null) {
            return activity.getExecutiveDate();
        }

        if (activity.getRecurrenceWeekOrdinal() == null || activity.getRecurrenceDayOfWeek() == null) {
            throw new BadRequestException("Activity must have either executive date or recurrence rule");
        }

        DayOfWeek targetDay = parseDayOfWeek(activity.getRecurrenceDayOfWeek());
        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);
        LocalDate nthWeekDay = firstOfMonth.with(TemporalAdjusters.firstInMonth(targetDay))
                .plusWeeks(activity.getRecurrenceWeekOrdinal() - 1);

        if (!nthWeekDay.isAfter(today)) {
            return nthWeekDay;
        }

        LocalDate prevMonth = firstOfMonth.minusMonths(1);
        nthWeekDay = prevMonth.with(TemporalAdjusters.firstInMonth(targetDay))
                .plusWeeks(activity.getRecurrenceWeekOrdinal() - 1);
        return nthWeekDay;
    }

    private DayOfWeek parseDayOfWeek(String code) {
        return switch (code) {
            case "MO" -> DayOfWeek.MONDAY;
            case "TU" -> DayOfWeek.TUESDAY;
            case "WE" -> DayOfWeek.WEDNESDAY;
            case "TH" -> DayOfWeek.THURSDAY;
            case "FR" -> DayOfWeek.FRIDAY;
            case "SA" -> DayOfWeek.SATURDAY;
            case "SU" -> DayOfWeek.SUNDAY;
            default -> DayOfWeek.MONDAY;
        };
    }

    private void validateActivity(CreateCollectivityActivityDto dto) {
        boolean hasExecutiveDate = dto.getExecutiveDate() != null;
        boolean hasRecurrenceRule = dto.getRecurrenceRule() != null
                && dto.getRecurrenceRule().getWeekOrdinal() != null
                && dto.getRecurrenceRule().getDayOfWeek() != null;

        if (hasExecutiveDate && hasRecurrenceRule) {
            throw new BadRequestException("Either executive date or recurrence rule must be provided, not both");
        }
        if (!hasExecutiveDate && !hasRecurrenceRule) {
            throw new BadRequestException("Either executive date or recurrence rule must be provided");
        }
    }

    private CollectivityActivity toEntity(CreateCollectivityActivityDto dto, String collectivityId) {
        CollectivityActivity activity = new CollectivityActivity();
        activity.setLabel(dto.getLabel());
        activity.setActivityType(dto.getActivityType());
        activity.setCollectivityId(collectivityId);

        if (dto.getExecutiveDate() != null) {
            activity.setExecutiveDate(dto.getExecutiveDate());
        }
        if (dto.getRecurrenceRule() != null) {
            activity.setRecurrenceWeekOrdinal(dto.getRecurrenceRule().getWeekOrdinal());
            activity.setRecurrenceDayOfWeek(dto.getRecurrenceRule().getDayOfWeek());
        }
        return activity;
    }

    private CollectivityActivityDto toDto(CollectivityActivity activity) {
        CollectivityActivityDto dto = new CollectivityActivityDto();
        dto.setId(activity.getId());
        dto.setLabel(activity.getLabel());
        dto.setActivityType(activity.getActivityType());
        dto.setMemberOccupationConcerned(activity.getMemberOccupationConcerned());
        dto.setExecutiveDate(activity.getExecutiveDate());

        if (activity.getRecurrenceWeekOrdinal() != null && activity.getRecurrenceDayOfWeek() != null) {
            MonthlyRecurrenceRuleDto rule = new MonthlyRecurrenceRuleDto();
            rule.setWeekOrdinal(activity.getRecurrenceWeekOrdinal());
            rule.setDayOfWeek(activity.getRecurrenceDayOfWeek());
            dto.setRecurrenceRule(rule);
        }
        return dto;
    }

    private ActivityMemberAttendanceDto toAttendanceDto(ActivityMemberAttendance attendance, Member member) {
        ActivityMemberAttendanceDto dto = new ActivityMemberAttendanceDto();
        dto.setId(attendance.getId());
        dto.setAttendanceStatus(attendance.getStatus());

        MemberDescriptionDto desc = new MemberDescriptionDto();
        desc.setId(member.getId());
        desc.setFirstName(member.getFirstName());
        desc.setLastName(member.getLastName());
        desc.setEmail(member.getEmail());
        if (member.getOccupation() != null) {
            desc.setOccupation(MemberOccupation.valueOf(member.getOccupation()));
        }
        dto.setMemberDescription(desc);

        return dto;
    }
}
