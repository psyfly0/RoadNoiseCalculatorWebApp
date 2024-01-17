package noise.road.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import noise.road.service.UserDataCleanupService;

@Service
public class CustomLogoutHandler implements LogoutHandler {

    private UserDataCleanupService userDataCleanupService;

    public CustomLogoutHandler(UserDataCleanupService userDataCleanupService) {
        this.userDataCleanupService = userDataCleanupService;
    }
    
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            if (userDetails.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))) {
                // Cleanup for ROLE_USER
                userDataCleanupService.cleanupUserData(userDetails.getUsername());
            } else if (userDetails.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_GUEST"))) {
                // Cleanup for ROLE_GUEST
                userDataCleanupService.deleteGuestSchema(userDetails.getUsername());
            }
        }
    }
}
