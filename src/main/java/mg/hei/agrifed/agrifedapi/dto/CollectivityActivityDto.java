package mg.hei.agrifed.agrifedapi.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityActivityDto {
    private String id;
    private String label;
    private String activityType;
    private List<String> memberOccupationConcerned;
    private MonthlyRecurrenceRuleDto recurrenceRule;
    private LocalDate executiveDate;
}
