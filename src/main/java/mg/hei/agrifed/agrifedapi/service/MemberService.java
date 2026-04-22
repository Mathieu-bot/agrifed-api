package mg.hei.agrifed.agrifedapi.service;

import mg.hei.agrifed.agrifedapi.dto.CreateMemberDto;
import mg.hei.agrifed.agrifedapi.dto.MemberDto;

import java.util.List;

public interface MemberService {
    List<MemberDto> createMembers(List<CreateMemberDto> members);
}