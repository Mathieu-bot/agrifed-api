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
public class CollectivityOverallStatisticsDto {
    private CollectivityInformationDto collectivityInformation;
    private Integer newMembersNumber;
    private BigDecimal overallMemberCurrentDuePercentage;
}
