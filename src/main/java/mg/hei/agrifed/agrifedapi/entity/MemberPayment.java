package mg.hei.agrifed.agrifedapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberPayment {
    private Integer id;
    private BigDecimal amount;
    private String paymentMode;
    private Integer memberId;
    private Integer membershipFeeId;
    private Integer accountCreditedId;
    private LocalDate creationDate;
}
