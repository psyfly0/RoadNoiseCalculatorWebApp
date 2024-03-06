package noise.road.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import noise.road.dto.DbfDataDTO;
import noise.road.service.DifferenceCalculatorService;
import noise.road.service.SortService;

@RestController
@RequestMapping("/sortAndDifferences")
@Slf4j
public class SortAndDifferencesController {
	
	@Autowired
	private DifferenceCalculatorService differenceCalcService;
	
	@Autowired
    private SortService sortService;

	@PostMapping("differences/{fileId1}/{fileId2}")
	public ResponseEntity<String> calculateDifferences(@PathVariable int fileId1, @PathVariable int fileId2, @RequestBody Map<String, Object> requestBody) {
		try {
//			log.info("fileId1: {}", fileId1);
	//		log.info("fileId2: {}", fileId2);
			
			int differenceColumnToUpdate = (int) requestBody.get("differenceColumnToUpdate");
//		    log.info("differenceColumnToUpdate: {}", differenceColumnToUpdate);
		    
			differenceCalcService.calculateDifferences(fileId1, fileId2, differenceColumnToUpdate);
			return ResponseEntity.ok("Difference-Calculation performed and database successfully modifed");
		} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
		} catch (IllegalArgumentException e) {
			log.error("Required parameters are missing", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a számítások elvégzése közben. Hibásan megadott paraméterek.");
		}
	}
	
	@GetMapping("/sortByLaeq/{activeFileId}")
    public ResponseEntity<?> displaySortedData(@PathVariable int activeFileId) {
		try {
			List<DbfDataDTO> sortedData = sortService.sortDbfDataAndResultsByLaeqNight(activeFileId);
	        return ResponseEntity.ok(sortedData);
		} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
		} catch (IllegalArgumentException e) {
			log.error("Required parameters are missing", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba az adatok rendezése közben. Hibásan megadott paraméterek.");
		}
    }
}
