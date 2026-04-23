package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.Contribution;

import java.util.List;

public interface ContributionRepository {
    Contribution save(Contribution contribution);
    List<Contribution> findByMemberId(String memberId);
}