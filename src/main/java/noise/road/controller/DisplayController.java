package noise.road.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import noise.road.dto.DisplayDataDTO;
import noise.road.service.DbfDataService;

@RestController
@RequestMapping("/console")
@Slf4j
public class DisplayController {
	
	@Autowired
    private DbfDataService dbfDataService;

	@GetMapping("/displayLatestFile")
    public List<DisplayDataDTO> displayLatestFile() {
		List<DisplayDataDTO> a = dbfDataService.getLatestSavedFile();
		log.info("latest file: {}", a);
        return dbfDataService.getLatestSavedFile();
    }
	
	@GetMapping("/displayData")
	public Map<Integer, List<DisplayDataDTO>> displayAll() {
		Map<Integer, List<DisplayDataDTO>> proba = dbfDataService.getAll();
		log.info("All files: {}", proba);
		return dbfDataService.getAll();
	}
	
}
