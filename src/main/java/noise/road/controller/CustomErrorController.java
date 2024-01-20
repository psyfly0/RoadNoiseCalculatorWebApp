package noise.road.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

	@RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response) {
		// Check if the user is already authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            // Manually clear the security context
            SecurityContextHolder.clearContext();

            // Invalidate the current session
            // The listener picks it up, and triggers the data deletion
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "error/error";
    }
	
	@RequestMapping("/accessDenied")
	public String handleAccessDeniedError(HttpServletRequest request, HttpServletResponse response) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            // Manually clear the security context
            SecurityContextHolder.clearContext();

            // Invalidate the current session
            // The listener picks it up, and triggers the data deletion
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        
	    return "error/accessDenied";
	}
}
