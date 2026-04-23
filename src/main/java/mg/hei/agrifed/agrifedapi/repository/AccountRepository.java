package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.AccountFull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<AccountFull> findById(String id);
    List<AccountFull> findByCollectivityId(String collectivityId);
    void updateBalance(String accountId, BigDecimal delta);
}
