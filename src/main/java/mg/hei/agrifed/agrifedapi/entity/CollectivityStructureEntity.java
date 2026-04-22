package mg.hei.agrifed.agrifedapi.entity;

import lombok.*;

/**
 * Entity mapping for collectivity_structure table.
 * Represents the bureau positions of a collectivity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityStructureEntity {
    private Integer id;
    private Integer collectivityId;
    private Integer presidentId;
    private Integer vicePresidentId;
    private Integer treasurerId;
    private Integer secretaryId;
}