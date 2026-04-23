package mg.hei.agrifed.agrifedapi.service.impl;

import lombok.AllArgsConstructor;
import mg.hei.agrifed.agrifedapi.dto.*;
import mg.hei.agrifed.agrifedapi.entity.*;
import mg.hei.agrifed.agrifedapi.exception.BadRequestException;
import mg.hei.agrifed.agrifedapi.exception.NotFoundException;
import mg.hei.agrifed.agrifedapi.repository.*;
import mg.hei.agrifed.agrifedapi.service.MemberPaymentService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class MemberPaymentServiceImpl implements MemberPaymentService {

    private final MemberPaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final MembershipFeeRepository feeRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public List<MemberPaymentDto> createPayments(Integer memberId, List<CreateMemberPaymentDto> dtos) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("Member not found: " + memberId));

        List<MemberPaymentDto> result = new ArrayList<>();

        for (CreateMemberPaymentDto dto : dtos) {
            if (dto.getAmount() == null || dto.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                throw new BadRequestException("Payment amount must be greater than 0");
            }
            if (dto.getPaymentMode() == null) {
                throw new BadRequestException("Payment mode is required");
            }

            Integer feeId = Integer.parseInt(dto.getMembershipFeeIdentifier());
            MembershipFee fee = feeRepository.findById(feeId)
                    .orElseThrow(() -> new NotFoundException("Membership fee not found: " + dto.getMembershipFeeIdentifier()));

            Integer accountId = Integer.parseInt(dto.getAccountCreditedIdentifier());
            AccountFull account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Account not found: " + dto.getAccountCreditedIdentifier()));

            MemberPayment payment = new MemberPayment();
            payment.setAmount(dto.getAmount());
            payment.setPaymentMode(dto.getPaymentMode().name());
            payment.setMemberId(memberId);
            payment.setMembershipFeeId(feeId);
            payment.setAccountCreditedId(accountId);
            payment.setCreationDate(LocalDate.now());
            MemberPayment saved = paymentRepository.save(payment);

            Transaction transaction = new Transaction();
            transaction.setAccountId(accountId);
            transaction.setAmount(dto.getAmount());
            transaction.setTransactionDate(LocalDate.now());
            transaction.setDescription("Payment by member " + memberId + " for fee " + feeId);
            transaction.setMemberId(memberId);
            transactionRepository.save(transaction);

            MemberPaymentDto responseDto = new MemberPaymentDto();
            responseDto.setId(String.valueOf(saved.getId()));
            responseDto.setAmount(saved.getAmount());
            responseDto.setPaymentMode(dto.getPaymentMode());
            responseDto.setCreationDate(saved.getCreationDate().toString());
            responseDto.setAccountCredited(toAccountDto(account));

            result.add(responseDto);
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
            dto.setBankName(a.getBankName());
            dto.setBankAccountNumber(a.getAccountNumber());
            dto.setBankAccountKey(a.getRibKey());
        } else if ("mobile_money".equals(a.getType())) {
            dto.setHolderName(a.getHolderName());
            dto.setMobileBankingService(a.getServiceName());
            dto.setMobileNumber(a.getPhoneNumber());
        }
        return dto;
    }
}