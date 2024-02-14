package noise.road.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/session")
@Slf4j
public class SessionExpiredController {
	
	@GetMapping("/check-session")
	public ResponseEntity<String> checkSession(HttpServletRequest request) {
	    HttpSession session = request.getSession(false); 
	    
	    if (session == null || (session.getAttribute("user") == null && session.getAttribute("guest") == null)) {
	    	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // HTTP 401 Unauthorized (session expired or not authenticated)
	    } else {
	        return ResponseEntity.ok().build(); // HTTP 200 OK (session active)
	    }
	}
}
