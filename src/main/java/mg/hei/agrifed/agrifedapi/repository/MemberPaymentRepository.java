package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.MemberPayment;

import java.util.List;

public interface MemberPaymentRepository {
    MemberPayment save(MemberPayment payment);
    List<MemberPayment> findByMemberId(Integer memberId);
}
