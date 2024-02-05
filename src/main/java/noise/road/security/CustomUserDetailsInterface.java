package noise.road.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetailsInterface extends UserDetails {
	String getEmail();
}
