package noise.road.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class FormController {

	@GetMapping("/login")
	public String myLogin(HttpServletRequest request, HttpServletResponse response, Model model, 
			@RequestParam(value = "logout", defaultValue = "false") boolean logout, 
			@RequestParam(value = "error", defaultValue = "false") boolean error,
			@RequestParam(name = "regSuccess", required = false) boolean regSuccess,
			@RequestParam(name = "sessionExpired", required = false) boolean sessionExpired) {
		
		model.addAttribute("logout", logout);
		model.addAttribute("error", error);
		model.addAttribute("regSuccess", regSuccess);
		model.addAttribute("sessionExpired", sessionExpired);

	
		// After the user logged in, can't reach this page, only by manually typing in "pageName/login" url.
		// The user shouldn't do it, but who knows...
		// If so, the user must be logged out manually, and clear the datas from the tables
		
		// Check if the user is already authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            // Manually clear the security context
            SecurityContextHolder.clearContext();

            // Invalidate the current session
            // The listener picks it up, and triggers the data deletion
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
		
		return "login";
	}
	
	@GetMapping("/logout")
	public String myLogout() {
		return "logout";
	}
	

	
}
