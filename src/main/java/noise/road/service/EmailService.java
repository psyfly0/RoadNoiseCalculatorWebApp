package noise.road.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import noise.road.dto.ContactForm;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	private String receiver;
	
	public void sendSimpleMail(ContactForm form, String username, String email) {
		if (username.equals("")) {
			username = "Unathurized user";
		}
		
		if (email.equals("")) {
			email = "Unathurized user";
		}
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(form.getEmail());
        message.setTo(receiver);
        message.setSubject(form.getSubject());
        message.setText("Name: " + form.getName() + "\nRegistered username: " + username + 
        		"\nSender's Email: " + form.getEmail() + "\nRegistered email: " + email + "\nMessage: " + form.getMessage());

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
	
}
