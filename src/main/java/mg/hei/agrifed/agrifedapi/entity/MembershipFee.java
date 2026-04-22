package mg.hei.agrifed.agrifedapi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembershipFee {
    private Integer id;
    private LocalDate eligibleFrom;
    private String frequency;
    private BigDecimal amount;
    private String label;
    private String status;
    private Integer collectivityId;
}
