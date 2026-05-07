package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);
    Optional<Member> findById(String id);
    Optional<Member> findByEmail(String email);
    List<Member> findAll();
    List<Member> findByIdIn(List<String> ids);
    Member update(Member member);
    void deleteById(String id);
    List<Member> findByCollectivityId(String collectivityId);

    List<Member> findActiveMembersByCollectivityId(String collectivityId);
}
