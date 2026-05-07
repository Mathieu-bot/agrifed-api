package mg.hei.agrifed.agrifedapi.service.impl;

import lombok.AllArgsConstructor;
import mg.hei.agrifed.agrifedapi.dto.CollectivityInformationDto;
import mg.hei.agrifed.agrifedapi.dto.CollectivityLocalStatisticsDto;
import mg.hei.agrifed.agrifedapi.dto.CollectivityOverallStatisticsDto;
import mg.hei.agrifed.agrifedapi.dto.MemberDescriptionDto;
import mg.hei.agrifed.agrifedapi.dto.MemberOccupation;
import mg.hei.agrifed.agrifedapi.entity.ActivityMemberAttendance;
import mg.hei.agrifed.agrifedapi.entity.Collectivity;
import mg.hei.agrifed.agrifedapi.entity.CollectivityActivity;
import mg.hei.agrifed.agrifedapi.entity.Member;
import mg.hei.agrifed.agrifedapi.entity.MembershipFee;
import mg.hei.agrifed.agrifedapi.exception.NotFoundException;
import mg.hei.agrifed.agrifedapi.repository.ActivityRepository;
import mg.hei.agrifed.agrifedapi.repository.AttendanceRepository;
import mg.hei.agrifed.agrifedapi.repository.CollectivityRepository;
import mg.hei.agrifed.agrifedapi.repository.ContributionRepository;
import mg.hei.agrifed.agrifedapi.repository.MemberRepository;
import mg.hei.agrifed.agrifedapi.repository.MembershipFeeRepository;
import mg.hei.agrifed.agrifedapi.service.StatisticsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final MemberRepository memberRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final ContributionRepository contributionRepository;
    private final CollectivityRepository collectivityRepository;
    private final ActivityRepository activityRepository;
    private final AttendanceRepository attendanceRepository;

    @Override
    public List<CollectivityLocalStatisticsDto> getLocalStatistics(
            String collectivityId, LocalDate from, LocalDate to) {

        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity not found: " + collectivityId));

        List<Member> activeMembers = memberRepository.findActiveMembersByCollectivityId(collectivityId);
        List<MembershipFee> activeFees = membershipFeeRepository.findActiveByCollectivityId(collectivityId);
        List<CollectivityActivity> activities = activityRepository.findAllByCollectivityId(collectivityId);

        List<ActivityMemberAttendance> allAttendances = attendanceRepository.findAllByCollectivityId(collectivityId);
        Map<String, Map<LocalDate, ActivityMemberAttendance>> attendanceByActivityAndDate = buildAttendanceMap(allAttendances);

        List<CollectivityLocalStatisticsDto> results = new ArrayList<>();

        for (Member member : activeMembers) {

            BigDecimal earnedAmount = contributionRepository
                    .sumByMemberAndCollectivityAndDateBetween(member.getId(), collectivityId, from, to);

            BigDecimal unpaidAmount = BigDecimal.ZERO;

            for (MembershipFee fee : activeFees) {
                long occurrences = countOccurrences(fee, from, to);
                if (occurrences == 0) {
                    continue;
                }

                BigDecimal expectedAmount = fee.getAmount().multiply(BigDecimal.valueOf(occurrences));

                BigDecimal paidAmount = contributionRepository
                        .sumByMemberAndFeeAndDateBetween(member.getId(), fee.getId(), from, to);

                BigDecimal delta = expectedAmount.subtract(paidAmount);
                if (delta.compareTo(BigDecimal.ZERO) > 0) {
                    unpaidAmount = unpaidAmount.add(delta);
                }
            }

            BigDecimal assiduityPercentage = calculateMemberAssiduity(
                    member, activities, from, to, attendanceByActivityAndDate);

            CollectivityLocalStatisticsDto dto = new CollectivityLocalStatisticsDto();
            dto.setMemberDescription(toMemberDescription(member));
            dto.setEarnedAmount(earnedAmount);
            dto.setUnpaidAmount(unpaidAmount);
            dto.setAssiduityPercentage(assiduityPercentage);

            results.add(dto);
        }

        return results;
    }

    @Override
    public List<CollectivityOverallStatisticsDto> getOverallStatistics(LocalDate from, LocalDate to) {
        List<Collectivity> allCollectivities = collectivityRepository.findAll();
        List<CollectivityOverallStatisticsDto> results = new ArrayList<>();

        for (Collectivity collectivity : allCollectivities) {
            int newMembersNumber = collectivityRepository
                    .countAdmissionsByCollectivityIdAndDateBetween(collectivity.getId(), from, to);

            List<Member> activeMembers = memberRepository
                    .findActiveMembersByCollectivityId(collectivity.getId());
            List<MembershipFee> activeFees = membershipFeeRepository
                    .findActiveByCollectivityId(collectivity.getId());
            List<CollectivityActivity> activities = activityRepository
                    .findAllByCollectivityId(collectivity.getId());
            List<ActivityMemberAttendance> allAttendances = attendanceRepository
                    .findAllByCollectivityId(collectivity.getId());
            Map<String, Map<LocalDate, ActivityMemberAttendance>> attendanceByActivityAndDate = buildAttendanceMap(allAttendances);

            int membersCurrent = 0;
            BigDecimal totalAssiduity = BigDecimal.ZERO;

            for (Member member : activeMembers) {
                boolean isCurrent = true;
                for (MembershipFee fee : activeFees) {
                    long occurrences = countOccurrences(fee, from, to);
                    if (occurrences == 0) {
                        continue;
                    }
                    BigDecimal expectedAmount = fee.getAmount().multiply(BigDecimal.valueOf(occurrences));
                    BigDecimal paidAmount = contributionRepository
                            .sumByMemberAndFeeAndDateBetween(member.getId(), fee.getId(), from, to);
                    if (expectedAmount.subtract(paidAmount).compareTo(BigDecimal.ZERO) > 0) {
                        isCurrent = false;
                        break;
                    }
                }
                if (isCurrent) {
                    membersCurrent++;
                }

                BigDecimal memberAssiduity = calculateMemberAssiduity(
                        member, activities, from, to, attendanceByActivityAndDate);
                totalAssiduity = totalAssiduity.add(memberAssiduity);
            }

            BigDecimal percentage;
            if (activeMembers.isEmpty()) {
                percentage = BigDecimal.ZERO;
            } else {
                percentage = BigDecimal.valueOf(membersCurrent)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(activeMembers.size()), 2, RoundingMode.HALF_UP);
            }

            BigDecimal overallAssiduity;
            if (activeMembers.isEmpty()) {
                overallAssiduity = BigDecimal.ZERO;
            } else {
                overallAssiduity = totalAssiduity
                        .divide(BigDecimal.valueOf(activeMembers.size()), 2, RoundingMode.HALF_UP);
            }

            CollectivityInformationDto info = new CollectivityInformationDto();
            info.setName(collectivity.getName());
            info.setNumber(collectivity.getNumber());

            CollectivityOverallStatisticsDto dto = new CollectivityOverallStatisticsDto();
            dto.setCollectivityInformation(info);
            dto.setNewMembersNumber(newMembersNumber);
            dto.setOverallMemberCurrentDuePercentage(percentage);
            dto.setOverallMemberAssiduityPercentage(overallAssiduity);

            results.add(dto);
        }

        return results;
    }

    private long countOccurrences(MembershipFee fee, LocalDate from, LocalDate to) {
        LocalDate eligibleFrom = fee.getEligibleFrom();
        if (eligibleFrom == null) {
            return 0;
        }

        LocalDate start = eligibleFrom.isAfter(from) ? eligibleFrom : from;

        if (start.isAfter(to)) {
            return 0;
        }

        String frequency = fee.getFrequency();
        if (frequency == null) {
            return 0;
        }

        return switch (frequency) {
            case "WEEKLY" -> ChronoUnit.WEEKS.between(start, to) + 1;
            case "MONTHLY" -> ChronoUnit.MONTHS.between(start, to) + 1;
            case "ANNUALLY" -> isAnniversaryInRange(eligibleFrom, from, to) ? 1 : 0;
            case "PUNCTUALLY" -> !eligibleFrom.isBefore(from) && !eligibleFrom.isAfter(to) ? 1 : 0;
            default -> 0;
        };
    }

    private boolean isAnniversaryInRange(LocalDate eligibleFrom, LocalDate from, LocalDate to) {
        int targetMonth = eligibleFrom.getMonthValue();
        int targetDay = eligibleFrom.getDayOfMonth();

        for (int year = from.getYear(); year <= to.getYear(); year++) {
            LocalDate anniversary;
            try {
                anniversary = LocalDate.of(year, targetMonth, targetDay);
            } catch (Exception e) {
                anniversary = LocalDate.of(year, targetMonth, targetDay - 1);
            }

            if (!anniversary.isBefore(from) && !anniversary.isAfter(to)) {
                return true;
            }
        }
        return false;
    }

    private MemberDescriptionDto toMemberDescription(Member member) {
        MemberDescriptionDto dto = new MemberDescriptionDto();
        dto.setId(member.getId());
        dto.setFirstName(member.getFirstName());
        dto.setLastName(member.getLastName());
        dto.setEmail(member.getEmail());

        if (member.getMembershipType() != null) {
            try {
                dto.setOccupation(MemberOccupation.valueOf(member.getMembershipType()));
            } catch (IllegalArgumentException e) {
                dto.setOccupation(MemberOccupation.JUNIOR);
            }
        }

        return dto;
    }

    private Map<String, Map<LocalDate, ActivityMemberAttendance>> buildAttendanceMap(
            List<ActivityMemberAttendance> allAttendances) {
        return allAttendances.stream().collect(Collectors.groupingBy(
                ActivityMemberAttendance::getActivityId,
                Collectors.toMap(ActivityMemberAttendance::getOccurrenceDate, a -> a, (a, b) -> b)
        ));
    }

    private List<LocalDate> expandRecurrence(CollectivityActivity activity, LocalDate from, LocalDate to) {
        if (activity.getExecutiveDate() != null) {
            if (!activity.getExecutiveDate().isBefore(from) && !activity.getExecutiveDate().isAfter(to)) {
                return List.of(activity.getExecutiveDate());
            }
            return List.of();
        }

        if (activity.getRecurrenceWeekOrdinal() == null || activity.getRecurrenceDayOfWeek() == null) {
            return List.of();
        }

        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = from.withDayOfMonth(1);
        LocalDate end = to.withDayOfMonth(to.lengthOfMonth());

        while (!current.isAfter(end)) {
            DayOfWeek targetDay = parseDayOfWeek(activity.getRecurrenceDayOfWeek());
            LocalDate firstOfMonth = current.withDayOfMonth(1);
            LocalDate nthWeekDay = firstOfMonth.with(TemporalAdjusters.firstInMonth(targetDay))
                    .plusWeeks(activity.getRecurrenceWeekOrdinal() - 1);

            if (!nthWeekDay.isBefore(from) && !nthWeekDay.isAfter(to)) {
                dates.add(nthWeekDay);
            }

            current = current.plusMonths(1);
        }

        return dates;
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

    private BigDecimal calculateMemberAssiduity(
            Member member,
            List<CollectivityActivity> activities,
            LocalDate from,
            LocalDate to,
            Map<String, Map<LocalDate, ActivityMemberAttendance>> attendanceMap) {

        if (activities.isEmpty()) {
            return BigDecimal.valueOf(100);
        }

        long totalOccurrences = 0;
        long attendedOccurrences = 0;

        for (CollectivityActivity activity : activities) {
            List<String> concerned = activity.getMemberOccupationConcerned();
            if (concerned == null || concerned.isEmpty()) {
                continue;
            }
            if (!concerned.contains(member.getMembershipType())) {
                continue;
            }

            List<LocalDate> occurrences = expandRecurrence(activity, from, to);
            if (occurrences.isEmpty()) {
                continue;
            }

            totalOccurrences += occurrences.size();

            Map<LocalDate, ActivityMemberAttendance> memberAttendances = attendanceMap
                    .getOrDefault(activity.getId(), Map.of());

            for (LocalDate occDate : occurrences) {
                ActivityMemberAttendance attendance = memberAttendances.get(occDate);
                if (attendance != null && "ATTENDED".equals(attendance.getStatus())) {
                    attendedOccurrences++;
                }
            }
        }

        if (totalOccurrences == 0) {
            return BigDecimal.valueOf(100);
        }

        return BigDecimal.valueOf(attendedOccurrences)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalOccurrences), 2, RoundingMode.HALF_UP);
    }
}
