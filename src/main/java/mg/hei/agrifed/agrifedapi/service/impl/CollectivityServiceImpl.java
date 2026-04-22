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
import mg.hei.agrifed.agrifedapi.exception.NotFoundException;
import mg.hei.agrifed.agrifedapi.repository.CollectivityRepository;
import mg.hei.agrifed.agrifedapi.repository.CollectivityStructureRepository;
import mg.hei.agrifed.agrifedapi.repository.MemberRepository;
import mg.hei.agrifed.agrifedapi.service.CollectivityService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CollectivityServiceImpl implements CollectivityService {

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

            List<String> memberIds = dto.getMembers();
            List<Member> memberEntities = validateAndGetMembers(memberIds);

            if (dto.getStructure() != null) {
                validateStructureMembers(dto.getStructure(), memberEntities);
            }

            String number = "COL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String name = "Collectivity " + number;

            Collectivity entity = new Collectivity();
            entity.setNumber(number);
            entity.setName(name);
            entity.setSpecialty("General");
            entity.setCity("Unknown");
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