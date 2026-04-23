package mg.hei.agrifed.agrifedapi.service.impl;

import mg.hei.agrifed.agrifedapi.dto.CreateMemberDto;
import mg.hei.agrifed.agrifedapi.dto.Gender;
import mg.hei.agrifed.agrifedapi.dto.MemberDto;
import mg.hei.agrifed.agrifedapi.entity.Collectivity;
import mg.hei.agrifed.agrifedapi.entity.Member;
import mg.hei.agrifed.agrifedapi.exception.BadRequestException;
import mg.hei.agrifed.agrifedapi.exception.BusinessRuleViolationException;
import mg.hei.agrifed.agrifedapi.exception.NotFoundException;
import mg.hei.agrifed.agrifedapi.repository.CollectivityRepository;
import mg.hei.agrifed.agrifedapi.repository.MemberRepository;
import mg.hei.agrifed.agrifedapi.service.MemberService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemberServiceImpl implements MemberService {

    private static final int REGISTRATION_FEE = 50000;
    private static final int DEFAULT_ANNUAL_DUES = 200000;

    private final MemberRepository memberRepository;
    private final CollectivityRepository collectivityRepository;

    public MemberServiceImpl(MemberRepository memberRepository, CollectivityRepository collectivityRepository) {
        this.memberRepository = memberRepository;
        this.collectivityRepository = collectivityRepository;
    }

    @Override
    public List<MemberDto> createMembers(List<CreateMemberDto> createMembers) {
        List<MemberDto> createdMembers = new ArrayList<>();

        for (CreateMemberDto dto : createMembers) {
            if (dto.getRegistrationFeePaid() == null || !dto.getRegistrationFeePaid()) {
                throw new BadRequestException("Registration fee of " + REGISTRATION_FEE + " Ar must be paid");
            }
            if (dto.getMembershipDuesPaid() == null || !dto.getMembershipDuesPaid()) {
                throw new BadRequestException("Annual dues of " + DEFAULT_ANNUAL_DUES + " Ar must be paid");
            }

            if (dto.getReferees() == null || dto.getReferees().size() < 2) {
                throw new BadRequestException("Member must have at least 2 sponsors");
            }

            if (dto.getCollectivityIdentifier() == null || dto.getCollectivityIdentifier().isBlank()) {
                throw new BadRequestException("Collectivity identifier is required");
            }

            Collectivity targetCollectivity = collectivityRepository.findByNumber(dto.getCollectivityIdentifier())
                    .orElseThrow(() -> new NotFoundException("Collectivity not found: " + dto.getCollectivityIdentifier()));

            List<String> refereeIds = dto.getReferees();
            List<Integer> sponsorIds = parseRefereeIds(refereeIds);

            List<Member> sponsorEntities = memberRepository.findByIdIn(sponsorIds);
            if (sponsorEntities.size() != sponsorIds.size()) {
                throw new NotFoundException("One or more sponsors not found");
            }

            validateSponsorDistribution(refereeIds, targetCollectivity);

            Member entity = new Member();
            entity.setFirstName(dto.getFirstName());
            entity.setLastName(dto.getLastName());
            entity.setBirthDate(LocalDate.parse(dto.getBirthDate()));
            entity.setGender(mapGenderToEntity(dto.getGender()));
            entity.setAddress(dto.getAddress());
            entity.setOccupation(dto.getProfession());
            entity.setPhone(String.valueOf(dto.getPhoneNumber()));
            entity.setEmail(dto.getEmail());
            entity.setMembershipDate(LocalDate.now());
            entity.setRegistrationFeePaid(dto.getRegistrationFeePaid());
            entity.setMembershipDuesPaid(dto.getMembershipDuesPaid());

            Member saved = memberRepository.save(entity);

            MemberDto response = mapToDto(saved);
            response.setReferees(sponsorEntities.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList()));

            createdMembers.add(response);
        }

        return createdMembers;
    }

    private void validateSponsorDistribution(List<String> refereeIds, Collectivity targetCollectivity) {
        int totalSponsors = refereeIds.size();

        List<Member> membersInTarget = memberRepository.findByCollectivityId(targetCollectivity.getId());

        int sponsorsFromCollectivity = 0;
        int sponsorsFromOtherCollectivities = 0;

        for (String refereeId : refereeIds) {
            Integer sponsorId = Integer.parseInt(refereeId);

            boolean isFromTargetCollectivity = false;
            for (Member m : membersInTarget) {
                if (m.getId().equals(sponsorId)) {
                    isFromTargetCollectivity = true;
                    break;
                }
            }

            if (isFromTargetCollectivity) {
                sponsorsFromCollectivity++;
            } else {
                sponsorsFromOtherCollectivities++;
            }
        }

        if (sponsorsFromCollectivity < 1) {
            throw new BusinessRuleViolationException(
                "At least one sponsor must be from the target collectivity",
                "SPONSOR_FROM_COLLECTIVITY_REQUIRED",
                java.util.Map.of("fromTarget", sponsorsFromCollectivity, "fromOther", sponsorsFromOtherCollectivities)
            );
        }
    }

    private List<Integer> parseRefereeIds(List<String> refereeIds) {
        return refereeIds.stream()
                .map(id -> {
                    try {
                        return Integer.parseInt(id);
                    } catch (NumberFormatException e) {
                        throw new BadRequestException("Invalid referee ID: " + id);
                    }
                })
                .collect(Collectors.toList());
    }

    private String mapGenderToEntity(Gender gender) {
        if (gender == null) return null;
        return gender.name();
    }

    private MemberDto mapToDto(Member entity) {
        MemberDto dto = new MemberDto();
        dto.setId(String.valueOf(entity.getId()));
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setBirthDate(entity.getBirthDate() != null ? entity.getBirthDate().toString() : null);
        dto.setGender(entity.getGender() != null ? Gender.valueOf(entity.getGender()) : null);
        dto.setAddress(entity.getAddress());
        dto.setProfession(entity.getOccupation());
        dto.setPhoneNumber(entity.getPhone());
        dto.setEmail(entity.getEmail());
        return dto;
    }
}
