package mg.hei.agrifed.agrifedapi.entity;

import lombok.*;

/**
 * Entity representing the bureau (executive structure) of a collectivity.
 * Contains positions: president, vice-president, treasurer, secretary.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityStructure {
    private Integer id;
    private Integer collectivityId;
    private Integer presidentId;
    private Integer vicePresidentId;
    private Integer treasurerId;
    private Integer secretaryId;
    
    // Embedded member references (populated from joins)
    private Member president;
    private Member vicePresident;
    private Member treasurer;
    private Member secretary;
}