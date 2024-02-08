package noise.road.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import noise.road.dto.ContactForm;
import noise.road.security.CustomUserDetailsInterface;
import noise.road.service.EmailService;

@Controller
@RequestMapping("/contact")
public class ContactController {
	
	@Autowired
    private EmailService emailService;
	
	@PostMapping("/submit")
    public String submitForm(ContactForm form, Model model) {
		String username = "";
        String email = "";
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated() && !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
			CustomUserDetailsInterface userDetails = (CustomUserDetailsInterface) authentication.getPrincipal();
			username = userDetails.getUsername();
			email = userDetails.getEmail();
		}
		boolean messageSent = false;
		try {
	        emailService.sendSimpleMail(form, username, email);
	        messageSent = true; 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		model.addAttribute("messageSent", messageSent);
        return "contact"; 
    }

}
