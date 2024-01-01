package noise.road.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
		try {
			calculationsService.calculateAll(fileId);
			return ResponseEntity.ok("ALL-Calculations performed and database successfully modifed");
		} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
		} catch (IllegalArgumentException e) {
			log.error("Required parameters are missing", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a számítások elvégzése közben. Hibásan megadott paraméterek.");
		}
	}
	
	@PostMapping("/laeq/{fileId}")
	public ResponseEntity<String> calculateLAeq(@PathVariable int fileId) {
		try {
			log.info("fileId: {}", fileId);
			calculationsService.calculateLAeq(fileId);	
			return ResponseEntity.ok("LAeq-Calculation performed and database successfully modifed");
		} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
		} catch (IllegalArgumentException e) {
			log.error("Required parameters are missing", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a számítások elvégzése közben. Hibásan megadott paraméterek.");
		}
	}
	
	@PostMapping("/lw/{fileId}")
	public ResponseEntity<String> calculateLW(@PathVariable int fileId) {
		try {
			calculationsService.calculateLw(fileId);
			return ResponseEntity.ok("LW-Calculation performed and database successfully modifed");
		} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
		} catch (IllegalArgumentException e) {
			log.error("Required parameters are missing", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a számítások elvégzése közben. Hibásan megadott paraméterek.");
		}
	}
	
	@PostMapping("/protectiveDistance/{fileId}")
	public ResponseEntity<String> calculateProtectiveDistance(@PathVariable int fileId) {
		try {
			calculationsService.calculateProtectiveDistance(fileId);
			return ResponseEntity.ok("ProtectiveDistance-Calculation performed and database successfully modifed");
		} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
		} catch (IllegalArgumentException e) {
			log.error("Required parameters are missing", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a számítások elvégzése közben. Hibásan megadott paraméterek.");
		}
	}
	
	@PostMapping("/impactArea/{fileId}")
	public ResponseEntity<String> calculateImpactArea(@PathVariable int fileId) {
		try {
			calculationsService.calculateImpactArea(fileId);
			return ResponseEntity.ok("ImpactArea-Calculation performed and database successfully modifed");
		} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
		} catch (IllegalArgumentException e) {
			log.error("Required parameters are missing", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a számítások elvégzése közben. Hibásan megadott paraméterek.");
		}
	}
	
	@PostMapping("givenDistance/{fileId}/{userInput}")
	public ResponseEntity<String> calculateNoiseAtGivenDistance(@PathVariable int fileId, @PathVariable double userInput) {
		try {
			log.info("fileId: {}", fileId);
			log.info("userInput: {}", userInput);
			calculationsService.calculateNoiseAtGivenDistance(fileId, userInput);
			return ResponseEntity.ok("NoiseAtGivenDistance-Calculation performed and database successfully modifed");
		} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
		} catch (IllegalArgumentException e) {
			log.error("Required parameters are missing", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a számítások elvégzése közben. Hibásan megadott paraméterek.");
		}
	}
	

}
