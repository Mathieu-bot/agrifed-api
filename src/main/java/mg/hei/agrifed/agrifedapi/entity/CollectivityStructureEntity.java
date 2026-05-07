package mg.hei.agrifed.agrifedapi.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityStructureEntity {
    private String id;
    private String collectivityId;
    private String presidentId;
    private String vicePresidentId;
    private String treasurerId;
    private String secretaryId;
}