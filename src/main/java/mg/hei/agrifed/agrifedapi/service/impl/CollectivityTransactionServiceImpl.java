package mg.hei.agrifed.agrifedapi.service.impl;

import lombok.AllArgsConstructor;
import mg.hei.agrifed.agrifedapi.dto.*;
import mg.hei.agrifed.agrifedapi.entity.*;
import mg.hei.agrifed.agrifedapi.exception.BadRequestException;
import mg.hei.agrifed.agrifedapi.exception.NotFoundException;
import mg.hei.agrifed.agrifedapi.repository.*;
import mg.hei.agrifed.agrifedapi.service.CollectivityTransactionService;
import mg.hei.agrifed.agrifedapi.util.EnumConverter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CollectivityTransactionServiceImpl implements CollectivityTransactionService {

    private final TransactionRepository transactionRepository;
    private final CollectivityRepository collectivityRepository;
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;

    @Override
    public List<CollectivityTransactionDto> getTransactions(Integer collectivityId, LocalDate from, LocalDate to) {
        collectivityRepository.findById(collectivityId)
                .orElseThrow(() -> new NotFoundException("Collectivity not found: " + collectivityId));

        if (from == null || to == null) {
            throw new BadRequestException("Query parameters 'from' and 'to' are mandatory");
        }
        if (from.isAfter(to)) {
            throw new BadRequestException("'from' date must be before 'to' date");
        }

        List<Transaction> transactions =
                transactionRepository.findByCollectivityIdAndDateBetween(collectivityId, from, to);

        List<CollectivityTransactionDto> result = new ArrayList<>();
        for (Transaction t : transactions) {
            CollectivityTransactionDto dto = new CollectivityTransactionDto();
            dto.setId(String.valueOf(t.getId()));
            dto.setCreationDate(t.getTransactionDate() != null ? t.getTransactionDate().toString() : null);
            dto.setAmount(t.getAmount());

            Optional<AccountFull> account = accountRepository.findById(t.getAccountId());
            account.ifPresent(a -> dto.setAccountCredited(toAccountDto(a)));

            if (t.getMemberId() != null) {
                memberRepository.findById(t.getMemberId())
                        .ifPresent(m -> dto.setMemberDebited(toMemberDto(m)));
            }
            result.add(dto);
        }
        return result;
    }

    private FinancialAccountDto toAccountDto(AccountFull a) {
        FinancialAccountDto dto = new FinancialAccountDto();
        dto.setId(String.valueOf(a.getId()));
        dto.setType(a.getType());
        dto.setAmount(a.getBalance());
        if ("bank".equals(a.getType())) {
            dto.setHolderName(a.getHolderName());
            dto.setBankName(EnumConverter.fromDb(a.getBankName(), Bank.class));
            dto.setBankCode(a.getBankCode());
            dto.setBankBranchCode(a.getBankBranchCode());
            dto.setBankAccountNumber(a.getAccountNumber());
            dto.setBankAccountKey(a.getRibKey());
        } else if ("mobile_money".equals(a.getType())) {
            dto.setHolderName(a.getHolderName());
            dto.setMobileBankingService(EnumConverter.fromDb(a.getServiceName(), MobileBankingService.class));
            dto.setMobileNumber(a.getPhoneNumber());
        }
        return dto;
    }

    private MemberDto toMemberDto(Member m) {
        MemberDto dto = new MemberDto();
        dto.setId(String.valueOf(m.getId()));
        dto.setFirstName(m.getFirstName());
        dto.setLastName(m.getLastName());
        dto.setEmail(m.getEmail());
        dto.setPhoneNumber(m.getPhone());
        dto.setAddress(m.getAddress());
        dto.setProfession(m.getOccupation());
        return dto;
    }
}