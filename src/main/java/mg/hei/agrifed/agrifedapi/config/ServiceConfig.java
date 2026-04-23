package mg.hei.agrifed.agrifedapi.config;

import mg.hei.agrifed.agrifedapi.repository.*;
import mg.hei.agrifed.agrifedapi.service.*;
import mg.hei.agrifed.agrifedapi.service.impl.*;
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

    @Bean
    public MembershipFeeService membershipFeeService(
            MembershipFeeRepository feeRepository,
            CollectivityRepository collectivityRepository) {
        return new MembershipFeeServiceImpl(feeRepository, collectivityRepository);
    }

    @Bean
    public CollectivityTransactionService collectivityTransactionService(
            TransactionRepository transactionRepository,
            CollectivityRepository collectivityRepository,
            AccountRepository accountRepository,
            MemberRepository memberRepository) {
        return new CollectivityTransactionServiceImpl(
                transactionRepository, collectivityRepository, accountRepository, memberRepository);
    }
}