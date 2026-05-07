package mg.hei.agrifed.agrifedapi.entity;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityActivity {
    private String id;
    private String label;
    private String activityType;
    private LocalDate executiveDate;
    private Integer recurrenceWeekOrdinal;
    private String recurrenceDayOfWeek;
    private List<String> memberOccupationConcerned;
    private String collectivityId;
    private String federationId;
}
