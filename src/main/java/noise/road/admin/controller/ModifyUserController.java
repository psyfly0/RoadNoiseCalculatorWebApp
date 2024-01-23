package noise.road.admin.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import noise.road.admin.listener.LoggedUser;
import noise.road.admin.model.ActiveUserStore;
import noise.road.admin.service.UserService;
import noise.road.authenticationModel.User;

@Controller
@RequestMapping("/admin")
public class ModifyUserController {

	@Autowired
    private UserService userService;
	
	@Autowired
    private ActiveUserStore activeUserStore;
    
    @GetMapping("/modifyUser/{userId}")
    public String modifyUser(@PathVariable Integer userId, Model model) {
    	
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);

        return "admin/modifyUser";
    }
    
    @PostMapping("/modifyUser")
    public String saveChanges(@ModelAttribute User modifiedUser) {
    	// check if the user is logged in
    	List<LoggedUser> loggedUsers = activeUserStore.getLoggedUsers();
    	List<String> loggedNames = loggedUsers.stream()
								                .map(LoggedUser::getUsername)
								                .collect(Collectors.toList());
    	
    	User user = userService.getUserById(modifiedUser.getId());
    	String oldName = user.getUsername();
    	String newName = modifiedUser.getUsername();
    	
    	if (!loggedNames.contains(oldName)) {
	        userService.updateUser(modifiedUser); 
	        
	        if (!oldName.equals(newName)) {
	        	userService.updateSchemaName(oldName, newName);
	        }
    	}
        return "redirect:/admin/registeredUsers";
    }
    
    @GetMapping("/deleteUser/{userId}")
    public String deleteUser(@PathVariable Integer userId) {
    	User user = userService.getUserById(userId);
    	String username = user.getUsername();
    	
        userService.deleteUser(userId, username);
        return "redirect:/admin/registeredUsers";
    }
}
