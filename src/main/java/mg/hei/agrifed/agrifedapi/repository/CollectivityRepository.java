package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.Collectivity;

import java.util.List;
import java.util.Optional;

public interface CollectivityRepository {
    Collectivity save(Collectivity collectivity);
    Optional<Collectivity> findById(String id);
    Optional<Collectivity> findByNumber(String number);
    Optional<Collectivity> findByName(String name);
    List<Collectivity> findAll();
    List<Collectivity> findByIdIn(List<String> ids);
    Collectivity update(Collectivity collectivity);
    void deleteById(String id);
    List<Collectivity> findByStatus(String status);
    List<Collectivity> findByFederationId(String federationId);
}
