package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.CollectivityActivity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ActivityRepository {
    List<CollectivityActivity> findAllByCollectivityId(String collectivityId);
    Optional<CollectivityActivity> findById(String id);
    Optional<CollectivityActivity> findByIdAndCollectivityId(String id, String collectivityId);
    CollectivityActivity save(CollectivityActivity activity);
    List<CollectivityActivity> saveAll(List<CollectivityActivity> activities);
    void saveOccupations(String activityId, List<String> occupations);
    List<String> findOccupationsByActivityId(String activityId);
}
