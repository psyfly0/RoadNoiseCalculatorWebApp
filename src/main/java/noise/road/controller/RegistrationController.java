package noise.road.controller;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import noise.road.authenticationModel.User;
import noise.road.dto.UserDTO;
import noise.road.security.MySimpleUrlAuthenticationSuccessHandler;
import noise.road.security.MyUserDetailsService;

@Controller
@RequestMapping("/registration")
@Slf4j
public class RegistrationController {
	
	@Autowired
	private MyUserDetailsService userDetailsService;

	private int guestCounter = 0;

	@GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserDTO());
        return "registration";
    }

    @PostMapping
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
	            return "redirect:/login"; // Redirect to a success page
	        default:
	            // Handle other cases or errors
	            return "error";
	    }
    }
    
    @PostMapping("/registerGuest")
    public String registerGuest(HttpServletRequest request) {
    	String username = "guest";
    	String incrementedUsername = username + guestCounter;
    	String password = "guest";
    	User newGuest = userDetailsService.registerNewUser(incrementedUsername, password, null);

    	try {
    		UserDetails userDetails = userDetailsService.loadUserByUsername(newGuest.getUsername());
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());		
    	} catch (Exception e) {
    		log.error("auth error: {}", e);
    	}
    	guestCounter++;

    	return "redirect:/console/display";

    }
}
