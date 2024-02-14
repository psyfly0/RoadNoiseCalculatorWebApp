package noise.road.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import noise.road.service.UserDataCleanupService;



@WebListener
@Slf4j
public class SessionListener  implements HttpSessionListener {

	@Autowired
    private UserDataCleanupService userDataCleanupService;
	
/*	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		session.setMaxInactiveInterval(600);
	}*/
    
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
    	
    	HttpSession session = event.getSession();
        
        SecurityContext securityContext = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
       
        if (securityContext != null) {
            Authentication authentication = securityContext.getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                String username = userDetails.getUsername();
                
		        // Perform cleanup actions based on user role
		        if (userDetails.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_GUEST"))) {
		            userDataCleanupService.deleteGuestSchema(username);
		        } else if (userDetails.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))) {
		            userDataCleanupService.cleanupUserData(username);
		        }
		        

            }
        }
        


        SecurityContextHolder.clearContext();
        log.info("context cleared");
        session.invalidate();
        log.info("session invalidated");
        

    }
}
