package mg.hei.agrifed.agrifedapi.entity;

import lombok.*;

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
    
    private Member president;
    private Member vicePresident;
    private Member treasurer;
    private Member secretary;
}