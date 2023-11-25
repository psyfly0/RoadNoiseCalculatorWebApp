package noise.road.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/console")
public class FileReadSaveController {
	
	@GetMapping()
	public String index() {
		return "console/console";
	}

}
