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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

        List<ActivityMemberAttendance> allAttendances = attendanceRepository.findAllByActivityId(collectivityId);

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

            BigDecimal assiduityPercentage = calculateMemberAssiduity(member, activities, collectivityId);

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

                BigDecimal memberAssiduity = calculateMemberAssiduity(member, null, collectivity.getId());
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

    private BigDecimal calculateMemberAssiduity(Member member, List<CollectivityActivity> activities, String collectivityId) {
        if (activities == null) {
            activities = activityRepository.findAllByCollectivityId(collectivityId);
        }

        if (activities.isEmpty()) {
            return BigDecimal.valueOf(100);
        }

        List<String> concernedOccupations = activities.stream()
                .filter(a -> a.getMemberOccupationConcerned() != null && !a.getMemberOccupationConcerned().isEmpty())
                .filter(a -> a.getMemberOccupationConcerned().contains(member.getMembershipType()))
                .map(CollectivityActivity::getId)
                .collect(Collectors.toList());

        if (concernedOccupations.isEmpty()) {
            return BigDecimal.valueOf(100);
        }

        Map<String, ActivityMemberAttendance> attendanceByActivityId = attendanceRepository
                .findAllByActivityId(collectivityId)
                .stream()
                .filter(a -> a.getMemberId().equals(member.getId()))
                .collect(Collectors.toMap(ActivityMemberAttendance::getActivityId, a -> a));

        long attended = concernedOccupations.stream()
                .filter(activityId -> {
                    ActivityMemberAttendance attendance = attendanceByActivityId.get(activityId);
                    return attendance != null && "ATTENDED".equals(attendance.getStatus());
                })
                .count();

        if (concernedOccupations.isEmpty()) {
            return BigDecimal.valueOf(100);
        }

        return BigDecimal.valueOf(attended)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(concernedOccupations.size()), 2, RoundingMode.HALF_UP);
    }
}
