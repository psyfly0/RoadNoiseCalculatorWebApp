package noise.road.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("console/upload")
	public String console() {
		return "console/upload";
	}
	
	@GetMapping("console/display")
	public String display() {
		return "console/display";
	}

}
