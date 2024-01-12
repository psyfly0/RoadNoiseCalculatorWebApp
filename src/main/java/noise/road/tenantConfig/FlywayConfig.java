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
                .locations("db/migration/default")
                .dataSource(dataSource)
                .schemas(TenantIdentifierResolver.DEFAULT_TENANT)
                .cleanDisabled(false)									// KI KELL MAJD VENNI, TESZTELÉSHEZ MARADJON CSAK
                .load();
        flyway.clean();													// KI KELL MAJD VENNI, TESZTELÉSHEZ MARADJON CSAK											
        flyway.migrate();
        return flyway;
    }
	
	@Bean
    CommandLineRunner commandLineRunner(UserRepository repository, DataSource dataSource) {
        return args -> {
            repository.findAll().forEach(user -> {
                String tenant = user.getUsername();
                Flyway flyway = Flyway.configure()
                        .locations("db/migration/tenants")
                        .dataSource(dataSource)
                        .schemas(tenant)
                        .cleanDisabled(false)
                        .load();
                flyway.clean();
                flyway.migrate();
            });
        };
    }
}
