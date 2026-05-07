package mg.hei.agrifed.agrifedapi.config;

import mg.hei.agrifed.agrifedapi.mapper.CollectivityMapper;
import mg.hei.agrifed.agrifedapi.mapper.FinancialAccountMapper;
import mg.hei.agrifed.agrifedapi.mapper.MemberMapper;
import mg.hei.agrifed.agrifedapi.mapper.MembershipFeeMapper;
import mg.hei.agrifed.agrifedapi.repository.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public MemberMapper memberMapper(MemberRepository memberRepository, SponsorshipRepository sponsorshipRepository) {
        return new MemberMapper(memberRepository, sponsorshipRepository);
    }

    @Bean
    public CollectivityMapper collectivityMapper(MemberRepository memberRepository,
                                                 CollectivityStructureRepository structureRepository,
                                                 MemberMapper memberMapper) {
        return new CollectivityMapper(memberRepository, structureRepository, memberMapper);
    }

    @Bean
    public MembershipFeeMapper membershipFeeMapper() {
        return new MembershipFeeMapper();
    }

    @Bean
    public FinancialAccountMapper financialAccountMapper() {
        return new FinancialAccountMapper();
    }
}