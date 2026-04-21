package mg.hei.agrifed.agrifedapi.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DataSourceConfig {

    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    @Bean
    public DataSource dataSource() {
        return new DataSource();
    }

    public static class DataSource {

        public Connection getConnection() {
            try {
                Class.forName("org.postgresql.Driver");
                return DriverManager.getConnection(
                        DOTENV.get("JDBC_URL"),
                        DOTENV.get("USERNAME"),
                        DOTENV.get("PASSWORD")
                );
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
            } catch (SQLException e) {
                throw new RuntimeException("Unable to connect to database", e);
            }
        }
    }
}