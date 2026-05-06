package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.MembershipFee;

import java.util.List;
import java.util.Optional;

public interface MembershipFeeRepository {
    MembershipFee save(MembershipFee fee);
    Optional<MembershipFee> findById(String id);
    List<MembershipFee> findByCollectivityId(String collectivityId);

    List<MembershipFee> findActiveByCollectivityId(String collectivityId);

    MembershipFee update(MembershipFee fee);
    void delete(String id);
}