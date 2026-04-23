package mg.hei.agrifed.agrifedapi.service;

import mg.hei.agrifed.agrifedapi.dto.CollectivityTransactionDto;
import java.time.LocalDate;
import java.util.List;

public interface CollectivityTransactionService {
    List<CollectivityTransactionDto> getTransactions(Integer collectivityId, LocalDate from, LocalDate to);
}