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
	
	@PostMapping("/all/{fileId}")
	public ResponseEntity<String> calculateAll(@PathVariable int fileId) {
		calculationsService.calculateAll(fileId);
		return ResponseEntity.ok("ALL-Calculations performed and database successfully modifed");
	}
	
	@PostMapping("/laeq/{fileId}")
	public ResponseEntity<String> calculateLAeq(@PathVariable int fileId) {
		log.info("fileId: {}", fileId);
		calculationsService.calculateLAeq(fileId);	
		return ResponseEntity.ok("LAeq-Calculation performed and database successfully modifed");
	}
	
	@PostMapping("/lw/{fileId}")
	public ResponseEntity<String> calculateLW(@PathVariable int fileId) {
		calculationsService.calculateLw(fileId);
		return ResponseEntity.ok("LW-Calculation performed and database successfully modifed");
	}
	
	@PostMapping("/protectiveDistance/{fileId}")
	public ResponseEntity<String> calculateProtectiveDistance(@PathVariable int fileId) {
		calculationsService.calculateProtectiveDistance(fileId);
		return ResponseEntity.ok("ProtectiveDistance-Calculation performed and database successfully modifed");
	}
	
	@PostMapping("/impactArea/{fileId}")
	public ResponseEntity<String> calculateImpactArea(@PathVariable int fileId) {
		calculationsService.calculateImpactArea(fileId);
		return ResponseEntity.ok("ImpactArea-Calculation performed and database successfully modifed");
	}
	
	@PostMapping("givenDistance/{fileId}/{userInput}")
	public ResponseEntity<String> calculateNoiseAtGivenDistance(@PathVariable int fileId, @PathVariable double userInput) {
		log.info("fileId: {}", fileId);
		log.info("userInput: {}", userInput);
		calculationsService.calculateNoiseAtGivenDistance(fileId, userInput);
		return ResponseEntity.ok("NoiseAtGivenDistance-Calculation performed and database successfully modifed");
	}
	

}
