package mg.hei.agrifed.agrifedapi.service.impl;

import mg.hei.agrifed.agrifedapi.dto.CollectivityDto;
import mg.hei.agrifed.agrifedapi.dto.CollectivityStructure;
import mg.hei.agrifed.agrifedapi.dto.CreateCollectivityDto;
import mg.hei.agrifed.agrifedapi.dto.CreateCollectivityStructure;
import mg.hei.agrifed.agrifedapi.dto.MemberDto;
import mg.hei.agrifed.agrifedapi.entity.Collectivity;
import mg.hei.agrifed.agrifedapi.entity.CollectivityStructureEntity;
import mg.hei.agrifed.agrifedapi.entity.Member;
import mg.hei.agrifed.agrifedapi.exception.BadRequestException;
import mg.hei.agrifed.agrifedapi.exception.BusinessRuleViolationException;
import mg.hei.agrifed.agrifedapi.exception.NotFoundException;
import mg.hei.agrifed.agrifedapi.repository.CollectivityRepository;
import mg.hei.agrifed.agrifedapi.repository.CollectivityStructureRepository;
import mg.hei.agrifed.agrifedapi.repository.MemberRepository;
import mg.hei.agrifed.agrifedapi.service.CollectivityService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CollectivityServiceImpl implements CollectivityService {

    private static final int MIN_MEMBERS = 10;
    private static final int MIN_SENIOR_MEMBERS = 5;
    private static final int MIN_SENIORITY_MONTHS = 6;

    private final CollectivityRepository collectivityRepository;
    private final CollectivityStructureRepository structureRepository;
    private final MemberRepository memberRepository;

    public CollectivityServiceImpl(
            CollectivityRepository collectivityRepository,
            CollectivityStructureRepository structureRepository,
            MemberRepository memberRepository) {
        this.collectivityRepository = collectivityRepository;
        this.structureRepository = structureRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public List<CollectivityDto> createCollectivities(List<CreateCollectivityDto> createCollectivities) {
        List<CollectivityDto> createdCollectivities = new ArrayList<>();

        for (CreateCollectivityDto dto : createCollectivities) {
            if (dto.getFederationApproval() == null || !dto.getFederationApproval()) {
                throw new BadRequestException("Collectivity requires federation approval");
            }

            if (dto.getStructure() == null) {
                throw new BadRequestException("Collectivity structure is required");
            }

            if (dto.getLocation() == null || dto.getLocation().isBlank()) {
                throw new BadRequestException("Location (city) is required");
            }

            if (dto.getSpecialty() == null || dto.getSpecialty().isBlank()) {
                throw new BadRequestException("Agricultural specialty is required");
            }

            List<String> memberIds = dto.getMembers();
            if (memberIds == null || memberIds.size() < MIN_MEMBERS) {
                throw new BusinessRuleViolationException(
                    "Collectivity must have at least " + MIN_MEMBERS + " members",
                    "COLLECTIVITY_MIN_MEMBERS",
                    java.util.Map.of("required", MIN_MEMBERS, "provided", memberIds != null ? memberIds.size() : 0)
                );
            }

            List<Member> memberEntities = validateAndGetMembers(memberIds);

            long seniorMembersCount = memberEntities.stream()
                .filter(m -> m.getMembershipDate() != null)
                .filter(m -> {
                    long months = ChronoUnit.MONTHS.between(m.getMembershipDate(), LocalDate.now());
                    return months >= MIN_SENIORITY_MONTHS;
                })
                .count();

            if (seniorMembersCount < MIN_SENIOR_MEMBERS) {
                throw new BusinessRuleViolationException(
                    "Collectivity must have at least " + MIN_SENIOR_MEMBERS + " members with " + MIN_SENIORITY_MONTHS + "+ months seniority",
                    "COLLECTIVITY_MIN_SENIOR_MEMBERS",
                    java.util.Map.of("required", MIN_SENIOR_MEMBERS, "provided", seniorMembersCount, "minMonths", MIN_SENIORITY_MONTHS)
                );
            }

            validateStructureMembers(dto.getStructure(), memberEntities);

            String number = "COL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String name = "Collectivity " + number;

            Collectivity entity = new Collectivity();
            entity.setNumber(number);
            entity.setName(name);
            entity.setSpecialty(dto.getSpecialty());
            entity.setCity(dto.getLocation());
            entity.setLocation(dto.getLocation());
            entity.setCreationDate(LocalDate.now());
            entity.setFederationId(1);
            entity.setStatus("pending");
            entity.setFederationApproval(dto.getFederationApproval());

            Collectivity saved = collectivityRepository.save(entity);

            if (dto.getStructure() != null) {
                saveStructure(saved.getId(), dto.getStructure());
            }

            CollectivityDto response = mapToDto(saved);

            if (dto.getStructure() != null) {
                CollectivityStructure structure = mapStructure(dto.getStructure());
                response.setStructure(structure);
            }

            if (memberIds != null && !memberIds.isEmpty()) {
                response.setMembers(memberEntities.stream()
                        .map(this::mapMemberToDto)
                        .collect(Collectors.toList()));
            }

            createdCollectivities.add(response);
        }

        return createdCollectivities;
    }

    private List<Member> validateAndGetMembers(List<String> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> memberInts = memberIds.stream()
                .map(id -> {
                    try {
                        return Integer.parseInt(id);
                    } catch (NumberFormatException e) {
                        throw new BadRequestException("Invalid member ID: " + id);
                    }
                })
                .collect(Collectors.toList());

        List<Member> memberEntities = memberRepository.findByIdIn(memberInts);
        if (memberEntities.size() != memberInts.size()) {
            throw new NotFoundException("One or more members not found");
        }

        return memberEntities;
    }

    private void validateStructureMembers(CreateCollectivityStructure structure, List<Member> members) {
        if (structure == null) return;

        List<Integer> memberIds = members.stream()
                .map(Member::getId)
                .collect(Collectors.toList());

        if (structure.getPresident() == null) {
            throw new BadRequestException("President position must be filled");
        }
        if (structure.getVicePresident() == null) {
            throw new BadRequestException("Vice President position must be filled");
        }
        if (structure.getTreasurer() == null) {
            throw new BadRequestException("Treasurer position must be filled");
        }
        if (structure.getSecretary() == null) {
            throw new BadRequestException("Secretary position must be filled");
        }

        if (structure.getPresident() != null && !memberIds.contains(Integer.parseInt(structure.getPresident()))) {
            throw new NotFoundException("President member not found");
        }
        if (structure.getVicePresident() != null && !memberIds.contains(Integer.parseInt(structure.getVicePresident()))) {
            throw new NotFoundException("Vice president member not found");
        }
        if (structure.getTreasurer() != null && !memberIds.contains(Integer.parseInt(structure.getTreasurer()))) {
            throw new NotFoundException("Treasurer member not found");
        }
        if (structure.getSecretary() != null && !memberIds.contains(Integer.parseInt(structure.getSecretary()))) {
            throw new NotFoundException("Secretary member not found");
        }
    }

    private void saveStructure(Integer collectivityId, CreateCollectivityStructure createStructure) {
        CollectivityStructureEntity entity = new CollectivityStructureEntity();
        entity.setCollectivityId(collectivityId);

        if (createStructure.getPresident() != null) {
            entity.setPresidentId(Integer.parseInt(createStructure.getPresident()));
        }
        if (createStructure.getVicePresident() != null) {
            entity.setVicePresidentId(Integer.parseInt(createStructure.getVicePresident()));
        }
        if (createStructure.getTreasurer() != null) {
            entity.setTreasurerId(Integer.parseInt(createStructure.getTreasurer()));
        }
        if (createStructure.getSecretary() != null) {
            entity.setSecretaryId(Integer.parseInt(createStructure.getSecretary()));
        }

        structureRepository.save(entity);
    }

    private CollectivityDto mapToDto(Collectivity entity) {
        CollectivityDto dto = new CollectivityDto();
        dto.setId(String.valueOf(entity.getId()));
        dto.setLocation(entity.getLocation());
        return dto;
    }

    private CollectivityStructure mapStructure(CreateCollectivityStructure createStructure) {
        CollectivityStructure structure = new CollectivityStructure();

        if (createStructure.getPresident() != null) {
            Integer id = Integer.parseInt(createStructure.getPresident());
            memberRepository.findById(id).ifPresent(m -> structure.setPresident(mapMemberToDto(m)));
        }
        if (createStructure.getVicePresident() != null) {
            Integer id = Integer.parseInt(createStructure.getVicePresident());
            memberRepository.findById(id).ifPresent(m -> structure.setVicePresident(mapMemberToDto(m)));
        }
        if (createStructure.getTreasurer() != null) {
            Integer id = Integer.parseInt(createStructure.getTreasurer());
            memberRepository.findById(id).ifPresent(m -> structure.setTreasurer(mapMemberToDto(m)));
        }
        if (createStructure.getSecretary() != null) {
            Integer id = Integer.parseInt(createStructure.getSecretary());
            memberRepository.findById(id).ifPresent(m -> structure.setSecretary(mapMemberToDto(m)));
        }

        return structure;
    }

    private MemberDto mapMemberToDto(Member entity) {
        MemberDto dto = new MemberDto();
        dto.setId(String.valueOf(entity.getId()));
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setBirthDate(entity.getBirthDate() != null ? entity.getBirthDate().toString() : null);
        dto.setAddress(entity.getAddress());
        dto.setProfession(entity.getOccupation());
        dto.setPhoneNumber(entity.getPhone());
        dto.setEmail(entity.getEmail());
        return dto;
    }
}
