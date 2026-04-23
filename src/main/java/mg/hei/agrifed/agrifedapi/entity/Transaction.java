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
public class Transaction {
    private String id;
    private String accountId;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private String description;
    private String memberId;
}
