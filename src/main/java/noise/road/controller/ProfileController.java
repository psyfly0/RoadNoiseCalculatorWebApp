package noise.road.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import noise.road.admin.listener.LoggedUser;
import noise.road.admin.model.ActiveUserStore;
import noise.road.authenticationModel.User;
import noise.road.security.CustomUserDetails;
import noise.road.security.MySimpleUrlAuthenticationSuccessHandler;
import noise.road.security.MyUserDetailsService;
import noise.road.service.ProfileService;

@Controller
@RequestMapping("/profile")
@Slf4j
public class ProfileController {
	
	@Autowired
    private MyUserDetailsService userDetailsService;
	
	@Autowired
	private ProfileService profileService;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
    ActiveUserStore activeUserStore;

	@GetMapping
    public String showProfilePage(Model model, Authentication authentication, 
    		@RequestParam(name = "usernameModSuccess", required = false) boolean usernameModSuccess,
    		@RequestParam(name = "emailModSuccess", required = false) boolean emailModSuccess,
    		@RequestParam(name = "passwordModSuccess", required = false) boolean passwordModSuccess) {
		
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String email = userDetails.getEmail();
        
        model.addAttribute("username", username);
        model.addAttribute("email", email);
        model.addAttribute("usernameModSuccess", usernameModSuccess);
        model.addAttribute("emailModSuccess", emailModSuccess);
        model.addAttribute("passwordModSuccess", passwordModSuccess);
        
        return "profile";
    }
	
	@PostMapping("/update-username")
    public String updateUsername(@RequestParam("newUsername") String newUsername, Authentication authentication, Model model, HttpServletRequest request, HttpServletResponse response) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    //    User user = profileService.getUserByName(userDetails.getUsername());
        String saveUsername = profileService.saveNewUsername(userDetails.getUsername(), newUsername);
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("showUsernameForm", true);
        
        switch (saveUsername) {
        	case "NewUsernameEmpty":
        		model.addAttribute("newUsernameEmptyError", "Új felhasználó név nem lett megadva.");
	            return "profile";
        	case "ExistingUserNotFound":
        		model.addAttribute("existingUserNotFoundError", "Nem sikerült a felhasználót lehívni az adatbázisból.");
	            return "profile";
        	case "UsernameAlreadyExists":
        		model.addAttribute("usernameAlreadyExistsError", "Felhasználó név már létezik. Válassz másikat!");
        		return "profile";
        	case "UsernameInvalidFormat":
        		model.addAttribute("usernameInvalidFormatError", "Csak angol ábécé karaktereit(szóközt, írásjelet NEM) és számokat lehet megadni");
        		return "profile";
        	case "SaveSuccess":
        		logout(authentication, request, response);
        		login(authentication, request, newUsername);
                jdbcTemplate.execute("ALTER SCHEMA IF EXISTS \"" + userDetails.getUsername() + "\" RENAME TO \"" + newUsername + "\"");

        		return "redirect:/profile?usernameModSuccess=true";
    		default:
    			return "error/error";
        }
    }
	
	@PostMapping("/update-email")
    public String updateEmail(@RequestParam("newEmail") String newEmail, Authentication authentication, Model model, HttpServletRequest request, HttpServletResponse response) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    //    User user = profileService.getUserByName(userDetails.getUsername());
        String saveEmail = profileService.saveNewEmail(userDetails.getUsername(), newEmail);
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("showEmailForm", true);
        
        switch (saveEmail) {
        	case "NewEmailEmpty":
        		model.addAttribute("newEmailEmptyError", "Új email-cím nem lett megadva.");
	            return "profile";
        	case "ExistingUserNotFound":
        		model.addAttribute("existingUserNotFoundError", "Nem sikerült a felhasználót lehívni az adatbázisból.");
	            return "profile";
        	case "EmailAlreadyExists":
        		model.addAttribute("emailAlreadyExistsError", "Email-cím már létezik. Válassz másikat!");
        		return "profile";
        	case "EmailInvalidFormat":
        		model.addAttribute("emailInvalidFormatError", "Hibás email formátum.");
        		return "profile";
        	case "SaveSuccess":
        		logout(authentication, request, response);
        		login(authentication, request, userDetails.getUsername());

        		return "redirect:/profile?emailModSuccess=true";
    		default:
    			return "error/error";
        }
    }
	
	@PostMapping("/update-password")
    public String updatePassword(@RequestParam("oldPassword") String oldPassword,
    							@RequestParam("newPassword") String newPassword,
    							@RequestParam("newPasswordConfirm") String newPasswordConfirm,
    							Authentication authentication, Model model, HttpServletRequest request, HttpServletResponse response) {
		
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    //    User user = profileService.getUserByName(userDetails.getUsername());
        String savePassword = profileService.saveNewPassword(userDetails.getUsername(), oldPassword, newPassword, newPasswordConfirm);
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("showPasswordForm", true);
        
        switch (savePassword) {
        	case "NewPasswordEmpty":
        		model.addAttribute("newPasswordEmptyError", "Új jelszó nem lett megadva.");
	            return "profile";
        	case "ExistingUserNotFound":
        		model.addAttribute("existingUserNotFoundError", "Nem sikerült a felhasználót lehívni az adatbázisból.");
	            return "profile";
        	case "OldPasswordsDontMatch":
        		model.addAttribute("oldPasswordsDontMatchError", "Régi jelszó nem egyezik az adatbázisban tárolttal.");
        		return "profile";
        	case "NewPasswordsDontMatch":
        		model.addAttribute("newPasswordsDontMatchError", "Új jelszavak nem egyeznek.");
        		return "profile";
        	case "OldAndNewAreSame":
        		model.addAttribute("oldAndNewAreSameError", "A régi és új jelszavak nem egyezhetnek");
        		return "profile";
        	case "SaveSuccess":
        		logout(authentication, request, response);
        		login(authentication, request, userDetails.getUsername());

        		return "redirect:/profile?passwordModSuccess=true";
    		default:
    			return "error/error";
        }
    }
	
	@PostMapping("/delete-user")
	public String deleteUser(Authentication authentication, Model model, HttpServletRequest request, HttpServletResponse response) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String username = userDetails.getUsername();
		String deleteUser = "";
		
		try {
			logout(authentication, request, response);
			deleteUser = profileService.deleteUser(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		switch (deleteUser) {
			case "ExistingUserNotFound":
        		model.addAttribute("existingUserNotFoundError", "Nem sikerült a felhasználót lehívni az adatbázisból.");
	            return "profile";
			case "ErrorDeleting":
				model.addAttribute("errorDeleting", "A törlés nem volt sikeres! Próbáld meg újra, vagy kérd meg az admint a törlésre.");
				return "profile";
			case "SuccessDeleting":
				return "redirect:/login?userDeleted=true";
			default:
				return "error/error";
		}

	}
	
	private void logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
		authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            // Manually clear the security context
            SecurityContextHolder.clearContext();

            // Invalidate the current session
            // The listener picks it up, and triggers the data deletion
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
	}
	
	private void login(Authentication authentication, HttpServletRequest request, String username) {
		try {
            UserDetails newUserDetails = userDetailsService.loadUserByUsername(username);
            authentication = new UsernamePasswordAuthenticationToken(newUserDetails, newUserDetails.getPassword(), newUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

    		// keep track of logged in users
    		HttpSession session = request.getSession(false);
            if (session != null) {
                LoggedUser user = new LoggedUser(authentication.getName(), getEmail(authentication), activeUserStore);
                activeUserStore.getLoggedUsers().add(user);
                session.setAttribute("user", user);
            }
        } catch (Exception e) {
    		log.error("auth error: {}", e);
    	}
	}
	
	private String getEmail(Authentication authentication) {
	    Object principal = authentication.getPrincipal();
	    if (principal instanceof CustomUserDetails) {
	        return ((CustomUserDetails) principal).getEmail();
	    } else {
	        // Handle other cases as needed
	        return null;
	    }
	}
	
}
