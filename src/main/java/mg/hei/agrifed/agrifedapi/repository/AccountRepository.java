package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.AccountFull;

import java.util.Optional;

public interface AccountRepository {
    Optional<AccountFull> findById(Integer id);
    void updateBalance(Integer accountId, java.math.BigDecimal delta);
}
