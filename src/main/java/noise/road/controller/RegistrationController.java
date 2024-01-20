package noise.road.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import noise.road.authenticationModel.User;
import noise.road.dto.UserDTO;
import noise.road.security.MyUserDetailsService;

@Controller
@RequestMapping("/registration")
@Slf4j
public class RegistrationController {

	@Autowired
	private MyUserDetailsService userDetailsService;

	private int guestCounter = 0;

	@GetMapping
    public String showRegistrationForm(HttpServletRequest request, HttpServletResponse response, Model model) {
		// After the user logged in, can't reach this page, only by manually typing in "pageName/registration" url.
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
		
		
        model.addAttribute("userDto", new UserDTO());
        return "registration";
    }

    @PostMapping
    @Transactional
    public String processRegistration(Model model, @ModelAttribute("userDto") @Valid UserDTO userDto, BindingResult result) {
        // Validate and process the userDto
        if (result.hasErrors()) {
            // Handle validation errors
            return "registration";
        }
        
        // Check if username or email already exists
        String userExists = userDetailsService.checkUsernameAndEmail(userDto.getUsername(), userDto.getEmail());
        
        switch (userExists) {
	        case "bothExists":
	            model.addAttribute("bothExistError", "Felhasználónév és e-mail már létezik.");
	            return "registration";
	        case "usernameExists":
	            model.addAttribute("usernameExistError", "Felhasználónév már létezik.");
	            return "registration";
	        case "emailExists":
	            model.addAttribute("emailExistError", "E-mail már létezik.");
	            return "registration";
	        case "noneExists":
	            User newUser = userDetailsService.registerNewUser(userDto.getUsername(), userDto.getPassword(), userDto.getEmail());
	            return "redirect:/login"; 
	        default:
	            return "error/error";
	    }
    }
    
    @PostMapping("/registerGuest")
    @Transactional
    public String registerGuest(HttpServletRequest request) {
    	String username = "guest";
    	String incrementedUsername = username + guestCounter;
    	String pass = "guest";
    	String password = pass + guestCounter;
    	User newGuest = userDetailsService.registerNewUser(incrementedUsername, password, null);
    	log.info("Authenticating guest: username={}, encodedPassword={}", username, newGuest.getPassword());
    	
    	try {    	    
    		UserDetails userDetails = userDetailsService.loadUserByUsername(newGuest.getUsername());
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            
            // sesssion timeout
            request.getSession().setMaxInactiveInterval(600);
            boolean isauth = authentication.isAuthenticated();
    	    log.info("is authenticated: {}", isauth);
    	} catch (Exception e) {
    		log.error("auth error: {}", e);
    	}
    	   	
    	guestCounter++;

    	return "redirect:/console/display";

    }
}
