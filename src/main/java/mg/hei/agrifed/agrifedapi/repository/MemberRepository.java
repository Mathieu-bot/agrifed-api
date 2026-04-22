package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);
    Optional<Member> findById(Integer id);
    Optional<Member> findByEmail(String email);
    List<Member> findAll();
    List<Member> findByIdIn(List<Integer> ids);
    Member update(Member member);
    void deleteById(Integer id);
    List<Member> findByCollectivityId(Integer collectivityId);
}
