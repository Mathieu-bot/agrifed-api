package mg.hei.agrifed.agrifedapi.service.impl;

import mg.hei.agrifed.agrifedapi.dto.CollectivityInformationDto;
import mg.hei.agrifed.agrifedapi.dto.CollectivityDto;
import mg.hei.agrifed.agrifedapi.dto.CollectivityStructure;
import mg.hei.agrifed.agrifedapi.dto.CreateCollectivityDto;
import mg.hei.agrifed.agrifedapi.dto.CreateCollectivityStructure;
import mg.hei.agrifed.agrifedapi.dto.MemberDto;
import mg.hei.agrifed.agrifedapi.entity.Collectivity;
import mg.hei.agrifed.agrifedapi.entity.CollectivityStructureEntity;
import mg.hei.agrifed.agrifedapi.entity.Member;
import mg.hei.agrifed.agrifedapi.exception.*;
import mg.hei.agrifed.agrifedapi.mapper.CollectivityMapper;
import mg.hei.agrifed.agrifedapi.mapper.MemberMapper;
import mg.hei.agrifed.agrifedapi.repository.CollectivityRepository;
import mg.hei.agrifed.agrifedapi.repository.CollectivityStructureRepository;
import mg.hei.agrifed.agrifedapi.repository.MemberRepository;
import mg.hei.agrifed.agrifedapi.service.CollectivityService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CollectivityServiceImpl implements CollectivityService {

    private static final int MIN_MEMBERS = 10;
    private static final int MIN_SENIOR_MEMBERS = 5;
    private static final int MIN_SENIORITY_MONTHS = 6;

    private final CollectivityRepository collectivityRepository;
    private final CollectivityStructureRepository structureRepository;
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final CollectivityMapper collectivityMapper;

    public CollectivityServiceImpl(
            CollectivityRepository collectivityRepository,
            CollectivityStructureRepository structureRepository,
            MemberRepository memberRepository,
            MemberMapper memberMapper,
            CollectivityMapper collectivityMapper) {
        this.collectivityRepository = collectivityRepository;
        this.structureRepository = structureRepository;
        this.memberRepository = memberRepository;
        this.memberMapper = memberMapper;
        this.collectivityMapper = collectivityMapper;
    }

    @Override
    public List<CollectivityDto> createCollectivities(List<CreateCollectivityDto> createCollectivities) {
        List<CollectivityDto> createdCollectivities = new ArrayList<>();

        for (CreateCollectivityDto dto : createCollectivities) {
            if (dto.getFederationApproval() != null && dto.getFederationApproval()) {
                throw new BadRequestException("federationApproval must be false or omitted during creation");
            }

            if (dto.getStructure() == null) {
                throw new BadRequestException("Collectivity structure is required");
            }

            if (dto.getLocation() == null || dto.getLocation().isBlank()) {
                throw new BadRequestException("Location (city) is required");
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

            Collectivity entity = new Collectivity();
            entity.setNumber(null);
            entity.setName(null);
            entity.setSpecialty(dto.getSpecialty());
            entity.setCity(dto.getLocation());
            entity.setLocation(dto.getLocation());
            entity.setCreationDate(LocalDate.now());
            entity.setFederationId("fed-1");
            entity.setStatus("PENDING");

            Collectivity saved = collectivityRepository.save(entity);

            if (dto.getStructure() != null) {
                saveStructure(saved.getId(), dto.getStructure());
            }

            CollectivityDto response = collectivityMapper.toDto(saved);

            if (dto.getStructure() != null) {
                CollectivityStructure structure = mapStructureFromEntity(saved.getId(), dto.getStructure());
                response.setStructure(structure);
            }

            if (!memberIds.isEmpty()) {
                response.setMembers(memberMapper.toDtoList(memberEntities));
            }

            createdCollectivities.add(response);
        }

        return createdCollectivities;
    }

    @Override
    public CollectivityDto assignNameAndNumber(String id, CollectivityInformationDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BadRequestException("Name is required");
        }
        if (dto.getNumber() == null || dto.getNumber().isBlank()) {
            throw new BadRequestException("Number is required");
        }

        Collectivity existing = collectivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collectivity", id));

        if (!"PENDING".equals(existing.getStatus())) {
            throw new BadRequestException("Collectivity already has name and number assigned - cannot be modified");
        }

        boolean nameExists = collectivityRepository.findByName(dto.getName()).isPresent();
        if (nameExists) {
            throw new ConflictException("Collectivity name already exists: " + dto.getName());
        }

        boolean numberExists = collectivityRepository.findByNumber(dto.getNumber()).isPresent();
        if (numberExists) {
            throw new ConflictException("Collectivity number already exists: " + dto.getNumber());
        }

        existing.setName(dto.getName());
        existing.setNumber(dto.getNumber());
        existing.setStatus("APPROVED");

        Collectivity updated = collectivityRepository.update(existing);
        return collectivityMapper.toDto(updated);
    }

    private List<Member> validateAndGetMembers(List<String> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Member> memberEntities = memberRepository.findByIdIn(memberIds);
        if (memberEntities.size() != memberIds.size()) {
            throw new NotFoundException("One or more members not found");
        }

        return memberEntities;
    }

    private void validateStructureMembers(CreateCollectivityStructure structure, List<Member> members) {
        if (structure == null) return;

        List<String> memberIds = members.stream()
                .map(Member::getId)
                .toList();

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

        if (!memberIds.contains(structure.getPresident())) {
            throw new NotFoundException("President member not found");
        }
        if (!memberIds.contains(structure.getVicePresident())) {
            throw new NotFoundException("Vice president member not found");
        }
        if (!memberIds.contains(structure.getTreasurer())) {
            throw new NotFoundException("Treasurer member not found");
        }
        if (!memberIds.contains(structure.getSecretary())) {
            throw new NotFoundException("Secretary member not found");
        }
    }

    private void saveStructure(String collectivityId, CreateCollectivityStructure createStructure) {
        CollectivityStructureEntity entity = new CollectivityStructureEntity();
        entity.setCollectivityId(collectivityId);

        if (createStructure.getPresident() != null) {
            entity.setPresidentId(createStructure.getPresident());
        }
        if (createStructure.getVicePresident() != null) {
            entity.setVicePresidentId(createStructure.getVicePresident());
        }
        if (createStructure.getTreasurer() != null) {
            entity.setTreasurerId(createStructure.getTreasurer());
        }
        if (createStructure.getSecretary() != null) {
            entity.setSecretaryId(createStructure.getSecretary());
        }

structureRepository.save(entity);
    }

    private CollectivityStructure mapStructureFromEntity(String collectivityId, CreateCollectivityStructure createStructure) {
        CollectivityStructure structure = new CollectivityStructure();

        if (createStructure.getPresident() != null) {
            String id = createStructure.getPresident();
            memberRepository.findById(id).ifPresent(m -> structure.setPresident(memberMapper.toDto(m)));
        }
        if (createStructure.getVicePresident() != null) {
            String id = createStructure.getVicePresident();
            memberRepository.findById(id).ifPresent(m -> structure.setVicePresident(memberMapper.toDto(m)));
        }
        if (createStructure.getTreasurer() != null) {
            String id = createStructure.getTreasurer();
            memberRepository.findById(id).ifPresent(m -> structure.setTreasurer(memberMapper.toDto(m)));
        }
        if (createStructure.getSecretary() != null) {
            String id = createStructure.getSecretary();
            memberRepository.findById(id).ifPresent(m -> structure.setSecretary(memberMapper.toDto(m)));
        }

        return structure;
    }

    @Override
    public CollectivityDto getById(String id) {
        Collectivity collectivity = collectivityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Collectivity not found: " + id));

        CollectivityDto dto = collectivityMapper.toDtoWithStructure(collectivity);

        List<Member> members = memberRepository.findByCollectivityId(id);
        dto.setMembers(memberMapper.toDtoList(members));

        return dto;
    }
}
