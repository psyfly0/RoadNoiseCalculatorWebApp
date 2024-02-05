package noise.road.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User implements CustomUserDetailsInterface {

    private final String email;

    public CustomUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String email) {
        super(username, password, enabled, true, true, true, authorities);
        this.email = email;
    }

    @Override
    public String getEmail() {
        return email;
    }
}
