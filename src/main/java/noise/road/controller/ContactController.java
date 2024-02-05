package noise.road.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import noise.road.dto.ContactForm;
import noise.road.service.EmailService;

@Controller
@RequestMapping("/contact")
public class ContactController {
	
	@Autowired
    private EmailService emailService;
	
	@PostMapping("/submit")
    public String submitForm(ContactForm form) {
		emailService.sendSimpleMail(form);
        return "contact"; 
    }

}
