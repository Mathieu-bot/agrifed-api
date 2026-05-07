package mg.hei.agrifed.agrifedapi.mapper;

import mg.hei.agrifed.agrifedapi.dto.Gender;
import mg.hei.agrifed.agrifedapi.dto.MemberDto;
import mg.hei.agrifed.agrifedapi.dto.MemberOccupation;
import mg.hei.agrifed.agrifedapi.entity.Member;
import mg.hei.agrifed.agrifedapi.entity.Sponsorship;
import mg.hei.agrifed.agrifedapi.repository.MemberRepository;
import mg.hei.agrifed.agrifedapi.repository.SponsorshipRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MemberMapper {

    private final MemberRepository memberRepository;
    private final SponsorshipRepository sponsorshipRepository;

    public MemberMapper(MemberRepository memberRepository, SponsorshipRepository sponsorshipRepository) {
        this.memberRepository = memberRepository;
        this.sponsorshipRepository = sponsorshipRepository;
    }

    public MemberDto toDto(Member member) {
        MemberDto dto = new MemberDto();
        dto.setId(member.getId());
        dto.setFirstName(member.getFirstName());
        dto.setLastName(member.getLastName());
        dto.setBirthDate(member.getBirthDate() != null ? member.getBirthDate().toString() : null);
        dto.setGender(toGenderDto(member.getGender()));
        dto.setAddress(member.getAddress());
        dto.setProfession(member.getOccupation());
        dto.setPhoneNumber(member.getPhone());
        dto.setEmail(member.getEmail());
        dto.setOccupation(toMemberOccupation(member.getMembershipType()));
        dto.setReferees(findReferees(member.getId()));
        return dto;
    }

    public List<MemberDto> toDtoList(List<Member> members) {
        if (members == null) return Collections.emptyList();
        return members.stream().map(this::toDto).collect(Collectors.toList());
    }

    private Gender toGenderDto(String gender) {
        if (gender == null || gender.isBlank()) return null;
        try {
            return Gender.valueOf(gender.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private MemberOccupation toMemberOccupation(String membershipType) {
        if (membershipType == null || membershipType.isBlank()) return null;
        try {
            return MemberOccupation.valueOf(membershipType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private List<MemberDto> findReferees(String memberId) {
        List<Sponsorship> sponsorships = sponsorshipRepository.findBySponsoredMemberId(memberId);
        return sponsorships.stream()
                .map(s -> memberRepository.findById(s.getSponsorMemberId()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}