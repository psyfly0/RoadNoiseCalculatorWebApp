package noise.road.service;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import noise.road.authenticationModel.User;
import noise.road.security.MyUserDetailsService;
import noise.road.service.util.PasswordGenerator;

@Service
@Slf4j
public class ForgotPasswordService {
	
	@Value("${spring.mail.username}")
	private String sender;
	
	private final static String SUBJECT = "KözútiZajSzámítóApp - Elfelejtett jelszó";

	@Autowired
    private MyUserDetailsService userDetailsService;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Transactional
	public String checkInputsForForgotPassword(String username, String email, String emailConfirm) {
		
		if (username == null || username.trim().isEmpty()) {
			return "UsernameEmpty";
		}
		
		if (email == null || email.trim().isEmpty()) {
			return "EmailEmpty";
		}
		
		if (emailConfirm == null || emailConfirm.trim().isEmpty()) {
			return "EmailConfirmEmpty";
		}
		
		if (!email.equals(emailConfirm)) {
			return "EmailsDontMatch";
		}
		
		User user = userDetailsService.getUserByUsername(username);
		if (user == null) {
            return "ExistingUserNotFound";
        }
		
		User userByEmail = userDetailsService.getUserByEmail(email);
		if (userByEmail == null) {
			return "ExistingEmailNotFound";
		}
		
		if (!user.getEmail().equals(email)) {
			return "UserHasDifferentEmail";
		}
		
		if (!userByEmail.getUsername().equals(username)) {
			return "EmailHasDifferentUser";
		}
		
		return "CheckSuccess";
	}
	
	@Transactional
	public boolean sendNewPassword(String username, String email) {
		String tempPassword = PasswordGenerator.generateRandomPassword();		
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(sender);
        message.setTo(email);
        message.setSubject(SUBJECT);
        message.setText("Az ideiglenes jelszavad: \n" + tempPassword + "\nBelépés után a \"Saját Profil\" oldalon változtasd meg!");
        
        try {
            javaMailSender.send(message);
        	User user = userDetailsService.getUserByUsername(username);
        	if (user != null) {
        		user.setPassword(new BCryptPasswordEncoder().encode(tempPassword));
        		userDetailsService.saveUser(user);
        	}
        	return true;
        } catch (MailException e) {
        	log.error("Failed to send message.", e);
        }
        return false;
        
        
	}

	
}
