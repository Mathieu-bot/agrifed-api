package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.MembershipFee;

import java.util.List;
import java.util.Optional;

public interface MembershipFeeRepository {
    List<MembershipFee> findByCollectivityId(Integer collectivityId);
    Optional<MembershipFee> findById(Integer id);
    MembershipFee save(MembershipFee membershipFee);
    void delete(Integer id);
}