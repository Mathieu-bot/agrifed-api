package mg.hei.agrifed.agrifedapi.service.impl;

import lombok.AllArgsConstructor;
import mg.hei.agrifed.agrifedapi.dto.*;
import mg.hei.agrifed.agrifedapi.dto.*;
import mg.hei.agrifed.agrifedapi.entity.AccountFull;
import mg.hei.agrifed.agrifedapi.entity.Contribution;
import mg.hei.agrifed.agrifedapi.entity.Member;
import mg.hei.agrifed.agrifedapi.entity.Transaction;
import mg.hei.agrifed.agrifedapi.exception.BadRequestException;
import mg.hei.agrifed.agrifedapi.exception.NotFoundException;
import mg.hei.agrifed.agrifedapi.repository.AccountRepository;
import mg.hei.agrifed.agrifedapi.repository.ContributionRepository;
import mg.hei.agrifed.agrifedapi.repository.MemberRepository;
import mg.hei.agrifed.agrifedapi.repository.TransactionRepository;
import mg.hei.agrifed.agrifedapi.service.MemberPaymentService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class MemberPaymentServiceImpl implements MemberPaymentService {

    private final MemberRepository memberRepository;
    private final ContributionRepository contributionRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<MemberPaymentDto> createPayments(Integer memberId, List<CreateMemberPaymentDto> payments) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found: " + memberId));

        List<MemberPaymentDto> result = new ArrayList<>();

        for (CreateMemberPaymentDto dto : payments) {
            if (dto.getAmount() == null || dto.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Amount must be greater than 0");
            }
            if (dto.getPaymentMode() == null) {
                throw new BadRequestException("Payment mode is required");
            }

            Integer accountCreditedId = null;
            if (dto.getAccountCreditedIdentifier() != null && !dto.getAccountCreditedIdentifier().isBlank()) {
                int accountId = Integer.parseInt(dto.getAccountCreditedIdentifier());
                AccountFull account = accountRepository.findById(accountId)
                        .orElseThrow(() -> new NotFoundException("Account not found: " + accountId));
                accountCreditedId = accountId;
            }

            Integer membershipFeeId = null;
            if (dto.getMembershipFeeIdentifier() != null && !dto.getMembershipFeeIdentifier().isBlank()) {
                membershipFeeId = Integer.parseInt(dto.getMembershipFeeIdentifier());
            }

            Contribution contribution = new Contribution();
            contribution.setAmount(dto.getAmount());
            contribution.setCollectionDate(LocalDate.now());
            contribution.setPaymentMethod(dto.getPaymentMode().name());
            contribution.setType("ONE_TIME");
            contribution.setFederationPercentage(java.math.BigDecimal.ZERO);
            contribution.setMemberId(memberId);
            contribution.setCollectivityId(null);
            contribution.setMembershipFeeId(membershipFeeId);
            contribution.setAccountCreditedId(accountCreditedId);
            contribution.setCreationDate(LocalDate.now());
            contribution.setLabel("Payment from member");

            Contribution saved = contributionRepository.save(contribution);

            if (accountCreditedId != null) {
                Transaction transaction = new Transaction();
                transaction.setAccountId(accountCreditedId);
                transaction.setAmount(dto.getAmount());
                transaction.setTransactionDate(LocalDate.now());
                transaction.setDescription("Payment from member " + memberId);
                transaction.setMemberId(memberId);
                transactionRepository.save(transaction);

                accountRepository.updateBalance(accountCreditedId, dto.getAmount());
            }

            MemberPaymentDto response = new MemberPaymentDto();
            response.setId(String.valueOf(saved.getId()));
            response.setAmount(dto.getAmount());
            response.setPaymentMode(dto.getPaymentMode());
            response.setCreationDate(saved.getCreationDate());

            if (accountCreditedId != null) {
                Optional<AccountFull> account = accountRepository.findById(accountCreditedId);
                account.ifPresent(a -> response.setAccountCredited(toAccountDto(a)));
            }

            result.add(response);
        }

        return result;
    }

    private FinancialAccountDto toAccountDto(AccountFull account) {
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