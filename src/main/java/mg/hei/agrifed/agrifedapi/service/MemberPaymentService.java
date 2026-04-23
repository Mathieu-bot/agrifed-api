package mg.hei.agrifed.agrifedapi.service;

import mg.hei.agrifed.agrifedapi.dto.CreateMemberPaymentDto;
import mg.hei.agrifed.agrifedapi.dto.MemberPaymentDto;

import java.util.List;

public interface MemberPaymentService {
    List<MemberPaymentDto> createPayments(Integer memberId, List<CreateMemberPaymentDto> payments);
}