package noise.road.tenantConfig;


import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import noise.road.repository.UserRepository;

@Configuration
public class FlywayConfig {

	@Bean
    Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .locations("db/migration/default", "db/migration/tenants")
                .dataSource(dataSource)
                .schemas(TenantIdentifierResolver.DEFAULT_TENANT)
                .load();										
        flyway.migrate();
        
	     // Run repair task after migrations
        Flyway repairFlyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas("admin")
                .load();
        repairFlyway.repair();

        return flyway;
	}
	
	@Bean
	CommandLineRunner commandLineRunner(UserRepository repository, DataSource dataSource) {
	    return args -> {
	        repository.findAll().forEach(user -> {
	        	 String tenant = user.getUsername();

	                if (!"admin".equals(tenant)) {

	                    Flyway flyway = Flyway.configure()
	                            .locations("db/migration/tenants")
	                            .dataSource(dataSource)
	                            .schemas(tenant)
	                            .load();
	                    flyway.migrate();
	                }
	        });
	        

	    };
	}
}
