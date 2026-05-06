package mg.hei.agrifed.agrifedapi.repository;

import mg.hei.agrifed.agrifedapi.entity.Sponsorship;

import java.util.List;

public interface SponsorshipRepository {
    List<Sponsorship> findBySponsoredMemberId(String memberId);
    List<Sponsorship> findBySponsorMemberId(String memberId);
}