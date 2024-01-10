package noise.road;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

//import noise.road.tenantConfig.TenantIdentifierResolver;

@SpringBootApplication
public class RoadNoiseCalculatorWebAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoadNoiseCalculatorWebAppApplication.class, args);
	}
	
	@Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
	
/*	@Bean
    public TenantIdentifierResolver sessionIdTenantResolver() {
        return new TenantIdentifierResolver();
    }*/

}
