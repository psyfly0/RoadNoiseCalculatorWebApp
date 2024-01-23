package noise.road.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
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
        
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
        
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
            	return "error/accessDenied";
            }
        }
        return "error/error";
    }

	
}
