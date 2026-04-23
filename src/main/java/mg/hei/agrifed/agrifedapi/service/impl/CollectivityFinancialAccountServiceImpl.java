package mg.hei.agrifed.agrifedapi.service.impl;

import lombok.AllArgsConstructor;
import mg.hei.agrifed.agrifedapi.dto.Bank;
import mg.hei.agrifed.agrifedapi.dto.FinancialAccountDto;
import mg.hei.agrifed.agrifedapi.dto.MobileBankingService;
import mg.hei.agrifed.agrifedapi.entity.AccountFull;
import mg.hei.agrifed.agrifedapi.exception.NotFoundException;
import mg.hei.agrifed.agrifedapi.repository.AccountRepository;
import mg.hei.agrifed.agrifedapi.repository.CollectivityRepository;
import mg.hei.agrifed.agrifedapi.service.CollectivityFinancialAccountService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CollectivityFinancialAccountServiceImpl implements CollectivityFinancialAccountService {

    private final AccountRepository accountRepository;
    private final CollectivityRepository collectivityRepository;

    @Override
    public List<FinancialAccountDto> getFinancialAccounts(Integer collectivityId, LocalDate at) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity not found: " + collectivityId));

        List<AccountFull> accounts = accountRepository.findByCollectivityId(collectivityId);

        return accounts.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private FinancialAccountDto toDto(AccountFull account) {
        FinancialAccountDto dto = new FinancialAccountDto();
        dto.setId(String.valueOf(account.getId()));
        dto.setType(account.getType());
        dto.setAmount(account.getBalance());

        if ("bank".equals(account.getType())) {
            dto.setHolderName(account.getHolderName());
            dto.setBankName(Bank.valueOf(account.getBankName()));
            dto.setBankCode(account.getBankCode());
            dto.setBankBranchCode(account.getBankBranchCode());
            dto.setBankAccountNumber(account.getAccountNumber());
            dto.setBankAccountKey(account.getRibKey());
        } else if ("mobile_money".equals(account.getType())) {
            dto.setHolderName(account.getHolderName());
            dto.setMobileBankingService(MobileBankingService.valueOf(account.getServiceName()));
            dto.setMobileNumber(account.getPhoneNumber());
        }

        return dto;
    }
}