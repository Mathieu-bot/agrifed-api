package mg.hei.agrifed.agrifedapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateCollectivityDto {
    private String location;
    private String specialty;
    private List<String> members;
    private CreateCollectivityStructure structure;
    private Boolean federationApproval;
}