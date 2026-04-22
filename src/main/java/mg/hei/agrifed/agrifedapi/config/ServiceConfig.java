package mg.hei.agrifed.agrifedapi.config;

import mg.hei.agrifed.agrifedapi.repository.CollectivityRepository;
import mg.hei.agrifed.agrifedapi.repository.CollectivityStructureRepository;
import mg.hei.agrifed.agrifedapi.repository.MemberRepository;
import mg.hei.agrifed.agrifedapi.service.CollectivityService;
import mg.hei.agrifed.agrifedapi.service.MemberService;
import mg.hei.agrifed.agrifedapi.service.impl.CollectivityServiceImpl;
import mg.hei.agrifed.agrifedapi.service.impl.MemberServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public CollectivityService collectivityService(
            CollectivityRepository collectivityRepository,
            CollectivityStructureRepository structureRepository,
            MemberRepository memberRepository) {
        return new CollectivityServiceImpl(collectivityRepository, structureRepository, memberRepository);
    }

    @Bean
    public MemberService memberService(
            MemberRepository memberRepository,
            CollectivityRepository collectivityRepository) {
        return new MemberServiceImpl(memberRepository, collectivityRepository);
    }
}