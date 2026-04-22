package mg.hei.agrifed.agrifedapi.service.impl;

import mg.hei.agrifed.agrifedapi.dto.CreateMemberDto;
import mg.hei.agrifed.agrifedapi.dto.Gender;
import mg.hei.agrifed.agrifedapi.dto.MemberDto;
import mg.hei.agrifed.agrifedapi.entity.Collectivity;
import mg.hei.agrifed.agrifedapi.entity.GenderEnum;
import mg.hei.agrifed.agrifedapi.entity.Member;
import mg.hei.agrifed.agrifedapi.exception.BadRequestException;
import mg.hei.agrifed.agrifedapi.exception.NotFoundException;
import mg.hei.agrifed.agrifedapi.repository.CollectivityRepository;
import mg.hei.agrifed.agrifedapi.repository.MemberRepository;
import mg.hei.agrifed.agrifedapi.service.MemberService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemberServiceImpl implements MemberService {

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
                throw new BadRequestException("Registration fee not paid");
            }
            if (dto.getMembershipDuesPaid() == null || !dto.getMembershipDuesPaid()) {
                throw new BadRequestException("Membership dues not paid");
            }

            if (dto.getReferees() == null || dto.getReferees().size() < 2) {
                throw new BadRequestException("Member must have at least 2 referees");
            }

            if (dto.getCollectivityIdentifier() == null || dto.getCollectivityIdentifier().isBlank()) {
                throw new BadRequestException("Collectivity identifier is required");
            }

            Collectivity collectivity = collectivityRepository.findByNumber(dto.getCollectivityIdentifier())
                    .orElseThrow(() -> new NotFoundException("Collectivity not found: " + dto.getCollectivityIdentifier()));

            List<String> refereeIds = dto.getReferees();
            List<Integer> refereeInts = parseMemberIds(refereeIds);

            List<Member> refereeEntities = memberRepository.findByIdIn(refereeInts);
            if (refereeEntities.size() != refereeInts.size()) {
                throw new NotFoundException("One or more referees not found");
            }

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
            response.setReferees(refereeEntities.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList()));

            createdMembers.add(response);
        }

        return createdMembers;
    }

    private List<Integer> parseMemberIds(List<String> memberIds) {
        return memberIds.stream()
                .map(id -> {
                    try {
                        return Integer.parseInt(id);
                    } catch (NumberFormatException e) {
                        throw new BadRequestException("Invalid member ID: " + id);
                    }
                })
                .collect(Collectors.toList());
    }

    private GenderEnum mapGenderToEntity(Gender gender) {
        if (gender == null) return null;
        return GenderEnum.valueOf(gender.name());
    }

    private MemberDto mapToDto(Member entity) {
        MemberDto dto = new MemberDto();
        dto.setId(String.valueOf(entity.getId()));
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setBirthDate(entity.getBirthDate() != null ? entity.getBirthDate().toString() : null);
        dto.setGender(entity.getGender() != null ? Gender.valueOf(entity.getGender().name()) : null);
        dto.setAddress(entity.getAddress());
        dto.setProfession(entity.getOccupation());
        dto.setPhoneNumber(entity.getPhone());
        dto.setEmail(entity.getEmail());
        return dto;
    }
}