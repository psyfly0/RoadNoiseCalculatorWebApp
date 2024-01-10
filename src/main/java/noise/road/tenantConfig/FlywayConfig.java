package noise.road.tenantConfig;


import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import noise.road.repository.UserRepository;

@Configuration
public class FlywayConfig {

	@Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .locations("db/default")
                .dataSource(dataSource)
                .schemas(TenantIdentifierResolver.DEFAULT_TENANT)
                .load();
        flyway.migrate();
        return flyway;
    }
	
	@Bean
    CommandLineRunner commandLineRunner(UserRepository repository, DataSource dataSource) {
        return args -> {
            repository.findAll().forEach(user -> {
                String tenant = user.getUsername();
                Flyway flyway = Flyway.configure()
                        .locations("db/tenants")
                        .dataSource(dataSource)
                        .schemas(tenant)
                        .load();
                flyway.migrate();
            });
        };
    }
}
