package mg.hei.agrifed.agrifedapi.config;

import mg.hei.agrifed.agrifedapi.mapper.CollectivityMapper;
import mg.hei.agrifed.agrifedapi.mapper.MemberMapper;
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
            MemberRepository memberRepository,
            MemberMapper memberMapper,
            CollectivityMapper collectivityMapper) {
        return new CollectivityServiceImpl(collectivityRepository, structureRepository, memberRepository, memberMapper, collectivityMapper);
    }

    @Bean
    public MemberService memberService(
            MemberRepository memberRepository,
            CollectivityRepository collectivityRepository,
            MemberMapper memberMapper) {
        return new MemberServiceImpl(memberRepository, collectivityRepository, memberMapper);
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

    @Bean
    public CollectivityFinancialAccountService collectivityFinancialAccountService(
            AccountRepository accountRepository,
            CollectivityRepository collectivityRepository) {
        return new CollectivityFinancialAccountServiceImpl(accountRepository, collectivityRepository);
    }

    @Bean
    public MemberPaymentService memberPaymentService(
            MemberRepository memberRepository,
            ContributionRepository contributionRepository,
            TransactionRepository transactionRepository,
            AccountRepository accountRepository) {
        return new MemberPaymentServiceImpl(
                memberRepository, contributionRepository, transactionRepository, accountRepository);
    }

    @Bean
    public StatisticsService statisticsService(
            MemberRepository memberRepository,
            MembershipFeeRepository membershipFeeRepository,
            ContributionRepository contributionRepository,
            CollectivityRepository collectivityRepository) {
        return new StatisticsServiceImpl(
                memberRepository, membershipFeeRepository, contributionRepository, collectivityRepository);
    }

    @Bean
    public ActivityService activityService(
            ActivityRepository activityRepository,
            AttendanceRepository attendanceRepository,
            MemberRepository memberRepository,
            CollectivityRepository collectivityRepository) {
        return new ActivityServiceImpl(
                activityRepository, attendanceRepository, memberRepository, collectivityRepository);
    }
}