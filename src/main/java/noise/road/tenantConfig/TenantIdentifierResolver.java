package noise.road.tenantConfig;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.UnknownKeyFor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

	static final String DEFAULT_TENANT = "default";

    @Override
    public String resolveCurrentTenantIdentifier() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Predicate.not(authentication -> authentication instanceof AnonymousAuthenticationToken))
                .map(Principal::getName)
                .orElse(DEFAULT_TENANT);
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}

/*
 * private ThreadLocal<String> currentTenant = ThreadLocal.withInitial(() -> "PUBLIC");
 * 
    public void setCurrentTenant(String sessionId) {
        currentTenant.set(sessionId);
    }
    
    @Override
    public String resolveCurrentTenantIdentifier() {
        return currentTenant.get();
    }
    
    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, this);
    }

	@Override
	public @UnknownKeyFor @NonNull @Initialized boolean validateExistingCurrentSessions() {
		return false;
	}
    
    
}
*/