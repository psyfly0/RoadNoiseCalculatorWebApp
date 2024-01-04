package noise.road.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FormController {

	@GetMapping("/login")
	public String myLoginAtLogout(Model model, 
			@RequestParam(value = "logout", defaultValue = "false") boolean logout, 
			@RequestParam(value = "error", defaultValue = "false") boolean error) {
		
		model.addAttribute("logout", logout);
		model.addAttribute("error", error);
		return "login";
	}
	
	@GetMapping("/logout")
	public String myLogout() {
		return "logout";
	}
	
	@GetMapping("/loginGuest")
	public String loginGuest(Model model, @RequestParam(value = "logout", defaultValue = "false") boolean logout, @RequestParam(value = "error", defaultValue = "false") boolean error) {
		model.addAttribute("logout", logout);
		model.addAttribute("error", error);
		return "loginGuest";
	}
}
