package noise.road.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import noise.road.authenticationModel.User;
import noise.road.dto.UserDTO;
import noise.road.security.MyUserDetailsService;

@Controller
//@RequestMapping("/registration")
public class RegistrationController {
	
	@Autowired
	private MyUserDetailsService userDetailsService;

	@GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserDTO());
        return "registration";
    }

    @PostMapping("/registration")
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
}
