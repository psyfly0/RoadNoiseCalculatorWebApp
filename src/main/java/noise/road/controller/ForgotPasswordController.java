package noise.road.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import noise.road.service.ForgotPasswordService;

@Controller
@RequestMapping("/forgotPassword")
public class ForgotPasswordController {
	
	@Autowired
	private ForgotPasswordService forgotPasswordService;
	
	@GetMapping
	public String showForgotPasswordPage() {
		return "forgotPassword";
	}

	@PostMapping("/form")
	public String forgotPassword(Model model,
			@RequestParam("username") String username,
			@RequestParam("email") String email,
			@RequestParam("emailConfirm") String emailConfirm) {
		
		
		String checkInputs = forgotPasswordService.checkInputsForForgotPassword(username, email, emailConfirm);
		
		switch (checkInputs) {
			case "UsernameEmpty":
				model.addAttribute("usernameEmptyError", "Felhasználó nevet meg kell adni.");
				return "forgotPassword";
			case "EmailEmpty":
				model.addAttribute("emailEmptyError", "Email-címet meg kell adni.");
				return "forgotPassword";
			case "EmailConfirmEmpty":
				model.addAttribute("emailConfirmEmptyError", "Email-címet meg kell erősíteni.");
				return "forgotPassword";
			case "EmailsDontMatch":
				model.addAttribute("emailsDontMatchError", "Email-címek nem egyeznek.");
				return "forgotPassword";
        	case "ExistingUserNotFound":
        		model.addAttribute("existingUserNotFoundError", "Nincs regisztrálva a(z) \"" + username + "\" nevű felhasználó.");
	            return "forgotPassword";
        	case "ExistingEmailNotFound":
        		model.addAttribute("existingEmailNotFoundError", "Nincs regisztrálva a(z) \"" + email + "\" email-cím.");
	            return "forgotPassword";
        	case "UserHasDifferentEmail":
        		model.addAttribute("userHasDifferentEmailError", "A felhasználó névhez (" + username + ") nem a megadott email (" + email + ") tartozik.");
	            return "forgotPassword";
        	case "EmailHasDifferentUser":
        		model.addAttribute("emailHasDifferentUserError", "Az email-címhez (" + email + ") nem a megadott felhasználó név (" + username + ") tartozik.");
	            return "forgotPassword";
        	case "CheckSuccess":
        		boolean success = forgotPasswordService.sendNewPassword(username, email);
        		if (success) {
        			return "redirect:/login?newPasswordSent=true";
        		} else {
        			model.addAttribute("emailSendingError", "Az új jelszót taralmazó email-t nem sikerült elküldeni! Ha a hiba fennáll, keresd az admint.");
        			return "forgotpassword";
        		}
    	    default:
    	    	return "error/error";
		}
	}
	
}


