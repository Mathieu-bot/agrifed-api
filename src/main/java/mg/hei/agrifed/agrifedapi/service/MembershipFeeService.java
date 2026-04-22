package mg.hei.agrifed.agrifedapi.service;

import mg.hei.agrifed.agrifedapi.dto.CreateMembershipFeeDto;
import mg.hei.agrifed.agrifedapi.dto.MembershipFeeDto;
import java.util.List;

public interface MembershipFeeService {
    List<MembershipFeeDto> getByCollectivity(Integer collectivityId);
    List<MembershipFeeDto> createForCollectivity(Integer collectivityId, List<CreateMembershipFeeDto> dtos);
}