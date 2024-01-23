package noise.road.admin.controller;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import noise.road.admin.listener.LoggedUser;
import noise.road.admin.model.ActiveUserStore;
import noise.road.admin.service.UserService;
import noise.road.authenticationModel.User;

@Controller
@RequestMapping("/admin")
public class UserController {

	@Autowired
    private ActiveUserStore activeUserStore;
	
	@Autowired
	private UserService userService;

	@GetMapping("/loggedInUsers")
    public String getLoggedUsers(Locale locale, Model model) {
		List<LoggedUser> loggedUsers = activeUserStore.getLoggedUsers();
		loggedUsers = loggedUsers.stream()
								 .filter(user -> !"admin".equals(user.getUsername()))
								 .collect(Collectors.toList());
		
		model.addAttribute("loggedUsers", loggedUsers);
        return "admin/loggedInUsers";
    }
	
	@GetMapping("/registeredUsers")
	public String getRegisteredUsers(Model model, @RequestParam(name = "search", required = false) String search) {
		List<User> registeredUsers = null;
		
		if (StringUtils.hasText(search)) {
			// Perform search by username or email
			registeredUsers = userService.findByUsernameContainingOrEmailContaining(search, search);
		} else {
			registeredUsers = userService.getAllUsers();
		}
		
	    registeredUsers = registeredUsers.stream()
	                                     .filter(user -> !"admin".equals(user.getUsername()))
	                                     .collect(Collectors.toList());

		model.addAttribute("registeredUsers", registeredUsers);
		return "admin/registeredUsers";		
	}

}
