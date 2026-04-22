package mg.hei.agrifed.agrifedapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CollectivityTransactionDto {
    private String id;
    private String creationDate;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private FinancialAccountDto accountCredited;
    private MemberDto memberDebited;
}
