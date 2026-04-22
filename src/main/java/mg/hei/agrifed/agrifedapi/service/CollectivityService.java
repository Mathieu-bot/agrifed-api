package mg.hei.agrifed.agrifedapi.service;

import mg.hei.agrifed.agrifedapi.dto.CollectivityInformationDto;
import mg.hei.agrifed.agrifedapi.dto.CollectivityDto;
import mg.hei.agrifed.agrifedapi.dto.CreateCollectivityDto;

import java.util.List;

public interface CollectivityService {
    List<CollectivityDto> createCollectivities(List<CreateCollectivityDto> collectivities);
    CollectivityDto assignNameAndNumber(String id, CollectivityInformationDto dto);
}