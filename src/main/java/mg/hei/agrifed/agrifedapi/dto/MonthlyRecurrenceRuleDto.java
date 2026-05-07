package mg.hei.agrifed.agrifedapi.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRecurrenceRuleDto {
    private Integer weekOrdinal;
    private String dayOfWeek;
}
