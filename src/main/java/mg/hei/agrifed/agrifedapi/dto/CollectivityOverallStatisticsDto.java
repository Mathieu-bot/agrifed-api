package mg.hei.agrifed.agrifedapi.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityOverallStatisticsDto {
    private CollectivityInformationShortDto collectivityInformation;
    private Integer newMembersNumber;
    private BigDecimal overallMemberCurrentDuePercentage;
}