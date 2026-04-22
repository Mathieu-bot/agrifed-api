package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.Collectivity;

import java.util.List;
import java.util.Optional;

public interface CollectivityRepository {
    Collectivity save(Collectivity collectivity);
    Optional<Collectivity> findById(Integer id);
    Optional<Collectivity> findByNumber(String number);
    List<Collectivity> findAll();
    List<Collectivity> findByIdIn(List<Integer> ids);
    Collectivity update(Collectivity collectivity);
    void deleteById(Integer id);
    List<Collectivity> findByStatus(String status);
    List<Collectivity> findByFederationId(Integer federationId);
}
