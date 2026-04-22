package mg.hei.agrifed.agrifedapi.config;

import mg.hei.agrifed.agrifedapi.config.DataSourceConfig.DataSource;
import mg.hei.agrifed.agrifedapi.repository.CollectivityRepository;
import mg.hei.agrifed.agrifedapi.repository.CollectivityStructureRepository;
import mg.hei.agrifed.agrifedapi.repository.MemberRepository;
import mg.hei.agrifed.agrifedapi.repository.MembershipFeeRepository;
import mg.hei.agrifed.agrifedapi.repository.impl.JdbcCollectivityRepositoryImpl;
import mg.hei.agrifed.agrifedapi.repository.impl.JdbcCollectivityStructureRepositoryImpl;
import mg.hei.agrifed.agrifedapi.repository.impl.JdbcMemberRepositoryImpl;
import mg.hei.agrifed.agrifedapi.repository.impl.JdbcMembershipFeeRepositoryImpl;
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
}