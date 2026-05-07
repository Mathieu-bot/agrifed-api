package mg.hei.agrifed.agrifedapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityLocalStatisticsDto {
    private MemberDescriptionDto memberDescription;
    private BigDecimal earnedAmount;
    private BigDecimal unpaidAmount;
    private BigDecimal assiduityPercentage;
}
