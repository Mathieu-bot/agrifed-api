package mg.hei.agrifed.agrifedapi.service;

import mg.hei.agrifed.agrifedapi.dto.FinancialAccountDto;

import java.time.LocalDate;
import java.util.List;

public interface CollectivityFinancialAccountService {
    List<FinancialAccountDto> getFinancialAccounts(Integer collectivityId, LocalDate at);
}