package mg.hei.agrifed.agrifedapi.config;

import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.repository.*;
import mg.hei.agrifed.agrifedapi.repository.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RepositoryConfig {

    @Bean
    public CollectivityRepository collectivityRepository(DataSource dataSource) {
        return new JdbcCollectivityRepositoryImpl(dataSource);
    }

    @Bean
    public MemberRepository memberRepository(DataSource dataSource) {
        return new JdbcMemberRepositoryImpl(dataSource);
    }

    @Bean
    public CollectivityStructureRepository collectivityStructureRepository(DataSource dataSource) {
        return new JdbcCollectivityStructureRepositoryImpl(dataSource);
    }

    @Bean
    public MembershipFeeRepository membershipFeeRepository(DataSource dataSource) {
        return new JdbcMembershipFeeRepositoryImpl(dataSource);
    }

    @Bean
    public MemberPaymentRepository memberPaymentRepository(DataSource dataSource){
        return new JdbcMemberPaymentRepositoryImpl(dataSource);
    }

    @Bean
    public AccountRepository accountRepository(DataSource dataSource){
        return new JdbcAccountRepositoryImpl(dataSource);
    }

    @Bean
    public TransactionRepository transactionRepository(DataSource dataSource){
        return new JdbcTransactionRepositoryImpl(dataSource);
    }
}