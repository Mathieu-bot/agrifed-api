package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.CollectivityStructureEntity;

import java.util.Optional;

public interface CollectivityStructureRepository {

    CollectivityStructureEntity save(CollectivityStructureEntity structure);
    Optional<CollectivityStructureEntity> findByCollectivityId(Integer collectivityId);

    void deleteByCollectivityId(Integer collectivityId);
    CollectivityStructureEntity update(CollectivityStructureEntity structure);
}
