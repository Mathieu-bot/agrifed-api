package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.MembershipFee;

import java.util.List;
import java.util.Optional;

public interface MembershipFeeRepository {
    List<MembershipFee> findByCollectivityId(Integer collectivityId);
    MembershipFee save(MembershipFee fee);
    Optional<MembershipFee> findById(Integer id);
}
