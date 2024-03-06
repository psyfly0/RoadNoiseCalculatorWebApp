package noise.road.tenantConfig;

import noise.road.listeners.SessionListener;

import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServletInitializer extends SpringBootServletInitializer {
	
	@Bean
    SessionListener sessionListener() {
        return new SessionListener();
    }
	
	
	

}
