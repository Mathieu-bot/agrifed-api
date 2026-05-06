package mg.hei.agrifed.agrifedapi.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityLocalStatisticsDto {
    private MemberDescriptionDto memberDescription;
    private BigDecimal earnedAmount;
    private BigDecimal unpaidAmount;
}