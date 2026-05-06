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
public class CreateMembershipFeeDto {
    private String eligibleFrom;
    private Frequency frequency;
    private BigDecimal amount;
    private String label;
}
