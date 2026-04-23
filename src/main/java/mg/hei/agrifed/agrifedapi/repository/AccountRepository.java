package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.AccountFull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<AccountFull> findById(Integer id);
    List<AccountFull> findByCollectivityId(Integer collectivityId);
    void updateBalance(Integer accountId, BigDecimal delta);
}
