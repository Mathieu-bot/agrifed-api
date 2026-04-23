package mg.hei.agrifed.agrifedapi.mapper;

import mg.hei.agrifed.agrifedapi.dto.CollectivityDto;
import mg.hei.agrifed.agrifedapi.dto.CollectivityStructure;
import mg.hei.agrifed.agrifedapi.entity.Collectivity;
import mg.hei.agrifed.agrifedapi.entity.CollectivityStructureEntity;
import mg.hei.agrifed.agrifedapi.entity.Member;
import mg.hei.agrifed.agrifedapi.repository.CollectivityStructureRepository;
import mg.hei.agrifed.agrifedapi.repository.MemberRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CollectivityMapper {

    private final MemberRepository memberRepository;
    private final CollectivityStructureRepository structureRepository;
    private final MemberMapper memberMapper;

    public CollectivityMapper(MemberRepository memberRepository,
                              CollectivityStructureRepository structureRepository,
                              MemberMapper memberMapper) {
        this.memberRepository = memberRepository;
        this.structureRepository = structureRepository;
        this.memberMapper = memberMapper;
    }

    public CollectivityDto toDto(Collectivity collectivity) {
        CollectivityDto dto = new CollectivityDto();
        dto.setId(collectivity.getId());
        dto.setName(collectivity.getName());
        dto.setNumber(collectivity.getNumber());
        dto.setLocation(collectivity.getLocation());
        dto.setStatus(collectivity.getStatus());
        return dto;
    }

    public CollectivityDto toDtoWithMembers(Collectivity collectivity, List<Member> members) {
        CollectivityDto dto = toDto(collectivity);
        dto.setMembers(memberMapper.toDtoList(members));
        return dto;
    }

    public CollectivityDto toDtoWithStructure(Collectivity collectivity) {
        CollectivityDto dto = toDto(collectivity);

        Optional<CollectivityStructureEntity> structureOpt = structureRepository.findByCollectivityId(collectivity.getId());
        if (structureOpt.isPresent()) {
            CollectivityStructureEntity entity = structureOpt.get();
            CollectivityStructure structure = new CollectivityStructure();

            if (entity.getPresidentId() != null) {
                memberRepository.findById(entity.getPresidentId())
                        .ifPresent(m -> structure.setPresident(memberMapper.toDto(m)));
            }
            if (entity.getVicePresidentId() != null) {
                memberRepository.findById(entity.getVicePresidentId())
                        .ifPresent(m -> structure.setVicePresident(memberMapper.toDto(m)));
            }
            if (entity.getTreasurerId() != null) {
                memberRepository.findById(entity.getTreasurerId())
                        .ifPresent(m -> structure.setTreasurer(memberMapper.toDto(m)));
            }
            if (entity.getSecretaryId() != null) {
                memberRepository.findById(entity.getSecretaryId())
                        .ifPresent(m -> structure.setSecretary(memberMapper.toDto(m)));
            }

            dto.setStructure(structure);
        }

        return dto;
    }
}