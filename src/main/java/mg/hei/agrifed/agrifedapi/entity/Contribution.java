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
public class Contribution {
    private Integer id;
    private BigDecimal amount;
    private LocalDate collectionDate;
    private String paymentMethod;
    private String type;
    private BigDecimal federationPercentage;
    private Integer memberId;
    private Integer collectivityId;
    private Integer membershipFeeId;
    private Integer accountCreditedId;
    private LocalDate creationDate;
    private String label;
}