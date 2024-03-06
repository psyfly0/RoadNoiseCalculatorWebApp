package noise.road.service;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import noise.road.authenticationModel.User;
import noise.road.security.MyUserDetailsService;

@Service
public class ProfileService {
	
	@Autowired
    private MyUserDetailsService userDetailsService;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
/*	public User getUserByName(String username) {
		return userDetailsService.getUserByUsername(username);
	}*/
	
	@Transactional
	public String saveNewUsername(String username, String newUsername) {
		
		jdbcTemplate.execute("SET SCHEMA 'admin'");
		
		if (newUsername == null || newUsername.trim().isEmpty()) {
            return "NewUsernameEmpty";
        }
		
		User user = userDetailsService.getUserByUsername(username);
		if (user == null) {
            return "ExistingUserNotFound";
        }
		
		User existingUser = userDetailsService.getUserByUsername(newUsername);
        if (existingUser != null) {
            return "UsernameAlreadyExists";
        }
        
        if (!Pattern.matches("^[a-zA-Z0-9]+$", newUsername)) {
        	return "UsernameInvalidFormat";
        }
        
		user.setUsername(newUsername);
		userDetailsService.saveUser(user);
		
		return "SaveSuccess";
	}
	
	@Transactional
	public String saveNewEmail(String username, String newEmail) {
		
		jdbcTemplate.execute("SET SCHEMA 'admin'");
		
		if (newEmail == null || newEmail.trim().isEmpty()) {
            return "NewEmailEmpty";
        }
		
		User user = userDetailsService.getUserByUsername(username);
		if (user == null) {
            return "ExistingUserNotFound";
        }
		
		User existingEmail = userDetailsService.getUserByEmail(newEmail);
        if (existingEmail != null) {
            return "EmailAlreadyExists";
        }
        
        if (!Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", newEmail)) {
        	return "EmailInvalidFormat";
        }
        
		user.setEmail(newEmail);
		userDetailsService.saveUser(user);
		
		return "SaveSuccess";
	}
	
	@Transactional
	public String saveNewPassword(String username, String oldPassword, String newPassword, String newPasswordConfirm) {
		
		jdbcTemplate.execute("SET SCHEMA 'admin'");
		
		if (newPassword == null || newPassword.trim().isEmpty() || newPasswordConfirm == null || newPasswordConfirm.trim().isEmpty()) {
            return "NewPasswordEmpty";
        }
		
		User user = userDetailsService.getUserByUsername(username);
		if (user == null) {
            return "ExistingUserNotFound";
        }
		
		if (!new BCryptPasswordEncoder().matches(oldPassword, user.getPassword())) {
			return "OldPasswordsDontMatch";
		}

		
		if (!newPassword.equals(newPasswordConfirm)) {
			return "NewPasswordsDontMatch";
		}
		
		if (oldPassword.equals(newPassword)) {
			return "OldAndNewAreSame";
		}
        
		user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
		userDetailsService.saveUser(user);
		
		return "SaveSuccess";
	}
	
	@Transactional
	public String deleteUser(String username) {
		jdbcTemplate.execute("SET SCHEMA 'admin'");
		
		boolean success = false;
		
		User user = userDetailsService.getUserByUsername(username);
		if (user == null) {
            return "ExistingUserNotFound";
        }
		try {
			userDetailsService.deleteUser(user);
			String dropSchemaSql = "DROP SCHEMA IF EXISTS \"" + username + "\" CASCADE";
			jdbcTemplate.execute(dropSchemaSql);
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (success) {
			return "SuccessDeleting";
		}
		return "ErrorDeleting";
	}

}
