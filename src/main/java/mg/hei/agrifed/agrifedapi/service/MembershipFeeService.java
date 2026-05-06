package mg.hei.agrifed.agrifedapi.service;

import mg.hei.agrifed.agrifedapi.dto.CreateMembershipFeeDto;
import mg.hei.agrifed.agrifedapi.dto.MembershipFeeDto;
import java.util.List;

public interface MembershipFeeService {
    List<MembershipFeeDto> getByCollectivity(String collectivityId);
    List<MembershipFeeDto> createForCollectivity(String collectivityId, List<CreateMembershipFeeDto> dtos);
}