package noise.road.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import noise.road.service.CalculationsService;

@RestController
@RequestMapping("/calculations")
@Slf4j
public class CalculationController {
	
	@Autowired
	private CalculationsService calculationsService;
	
	@PostMapping("/LAeq/{fileId}")
	public ResponseEntity<String> calculateLAeq(@PathVariable int fileId) {
		// call the service
		

		// display.html-t javítani: csak akkor küld fileId requestet, ha előtte klikkeltünk egy tabra
		
		
		calculationsService.calculateLAeq(fileId);
	
		return ResponseEntity.ok("Calculation performer and database successfully modifed");
	}
	

}
