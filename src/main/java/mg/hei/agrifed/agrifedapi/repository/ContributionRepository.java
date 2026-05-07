package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.Contribution;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ContributionRepository {
    Contribution save(Contribution contribution);
    List<Contribution> findByMemberId(String memberId);

    BigDecimal sumByMemberAndCollectivityAndDateBetween(
        String memberId, String collectivityId, LocalDate from, LocalDate to);

    BigDecimal sumByMemberAndFeeAndDateBetween(
        String memberId, String membershipFeeId, LocalDate from, LocalDate to);
}