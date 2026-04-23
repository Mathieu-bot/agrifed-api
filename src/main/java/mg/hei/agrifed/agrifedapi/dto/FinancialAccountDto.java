package mg.hei.agrifed.agrifedapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinancialAccountDto {
    private String id;
    private String type;
    private BigDecimal amount;
    private String holderName;
    private String mobileBankingService;
    private String mobileNumber;
    private String bankName;
    private String bankCode;
    private String bankBranchCode;
    private String bankAccountNumber;
    private String bankAccountKey;
}
