package mg.hei.agrifed.agrifedapi.mapper;

import mg.hei.agrifed.agrifedapi.dto.Bank;
import mg.hei.agrifed.agrifedapi.dto.FinancialAccountDto;
import mg.hei.agrifed.agrifedapi.dto.MobileBankingService;
import mg.hei.agrifed.agrifedapi.entity.AccountFull;

public class FinancialAccountMapper {

    public FinancialAccountDto toDto(AccountFull account) {
        FinancialAccountDto dto = new FinancialAccountDto();
        dto.setId(account.getId());
        dto.setType(account.getType());
        dto.setAmount(account.getBalance());

        if ("bank".equals(account.getType())) {
            dto.setHolderName(account.getHolderName());
            dto.setBankName(toBankDto(account.getBankName()));
            dto.setBankCode(account.getBankCode());
            dto.setBankBranchCode(account.getBankBranchCode());
            dto.setBankAccountNumber(account.getAccountNumber());
            dto.setBankAccountKey(account.getRibKey());
        } else if ("mobile_money".equals(account.getType())) {
            dto.setHolderName(account.getHolderName());
            dto.setMobileBankingService(toMobileBankingServiceDto(account.getServiceName()));
            dto.setMobileNumber(account.getPhoneNumber());
        }

        return dto;
    }

    private Bank toBankDto(String bankName) {
        if (bankName == null || bankName.isBlank()) return null;
        try {
            return Bank.valueOf(bankName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private MobileBankingService toMobileBankingServiceDto(String serviceName) {
        if (serviceName == null || serviceName.isBlank()) return null;
        try {
            return MobileBankingService.valueOf(serviceName.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}