package mg.hei.agrifed.agrifedapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectivityStructure {
    private MemberDto president;
    private MemberDto vicePresident;
    private MemberDto treasurer;
    private MemberDto secretary;
}