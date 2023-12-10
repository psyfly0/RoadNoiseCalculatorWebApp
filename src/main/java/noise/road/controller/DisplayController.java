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
import noise.road.dto.DbfDataDTO;
import noise.road.dto.DisplayDataDTO;
import noise.road.service.SaveDisplayService;

@RestController
@RequestMapping("/console")
@Slf4j
public class DisplayController {
	
	@Autowired
    private SaveDisplayService saveDisplayService;

	@GetMapping("/displayLatestFile")
    public List<DbfDataDTO> displayLatestFile() {
		List<DbfDataDTO> a = saveDisplayService.getLatestSavedFile();
		log.info("latest file: {}", a);
        return saveDisplayService.getLatestSavedFile();
    }
	
	@GetMapping("/displayData")
	public Map<Integer, List<DbfDataDTO>> displayAll() {
		Map<Integer, List<DbfDataDTO>> proba = saveDisplayService.getAll();
		log.info("All files: {}", proba);
		return saveDisplayService.getAll();
	}
	
}
