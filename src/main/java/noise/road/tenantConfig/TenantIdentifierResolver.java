package noise.road.tenantConfig;

import java.security.Principal;
import java.util.Optional;
import java.util.function.Predicate;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

	public static final String DEFAULT_TENANT = "admin";

    @Override
    public String resolveCurrentTenantIdentifier() {
	    String resolvedTenantIdentifier = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
		            .filter(Predicate.not(authentication -> authentication instanceof AnonymousAuthenticationToken))
		            .map(Principal::getName)
		            .orElse(DEFAULT_TENANT);
		    log.info("Resolved current tenant identifier: {}", resolvedTenantIdentifier);
		return resolvedTenantIdentifier;
	}

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
