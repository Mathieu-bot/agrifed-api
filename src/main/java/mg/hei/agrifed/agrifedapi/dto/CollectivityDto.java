package mg.hei.agrifed.agrifedapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CollectivityDto {
    private String id;
    private String location;
    private CollectivityStructure structure;
    private List<MemberDto> members;
}