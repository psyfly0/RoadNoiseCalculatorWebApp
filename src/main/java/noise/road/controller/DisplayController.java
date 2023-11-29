package noise.road.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import noise.road.dto.DisplayDataDTO;
import noise.road.service.DbfDataService;

@RestController
@RequestMapping("/console")
public class DisplayController {
	
	@Autowired
    private DbfDataService dbfDataService;

	/*@GetMapping("/display")
	public String display() {
		return "console/display";
	}*/
	
	@PostMapping("/displayLatestFile")
    public List<DisplayDataDTO> displayLatestFile() {
        return dbfDataService.getLatestSavedFile();
    }
	
}
