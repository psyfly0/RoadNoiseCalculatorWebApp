package noise.road.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import noise.road.dto.MutableParametersDTO;
import noise.road.service.ModificationService;

@RestController
@RequestMapping("/modification")
@Slf4j
public class ModificationController {

	@Autowired
	private ModificationService modificationService;
	
	@PutMapping("/cellValue/{activeFileId}/{row}/{columnName}/{updatedCellValue}")
	public ResponseEntity<String> modifyCellValue(@PathVariable int activeFileId,
													@PathVariable int row,
													@PathVariable String columnName,
													@PathVariable Integer updatedCellValue) {
		
		try {
		
			log.info("activeFileId, row, columnName, updatedCellValue: {}", activeFileId, row, columnName, updatedCellValue);
			modificationService.modifyCellValue(activeFileId, row, columnName, updatedCellValue);
			
			return ResponseEntity.ok("Cell modification performed and saved it to the database");
		} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
		} catch (IllegalArgumentException e) {
			log.error("Required parameters are missing", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a módosítások elvégzése közben. Hibásan megadott paraméterek.");
		} catch (NoSuchMethodException e) {
		    log.error("Setter method not found for column: {}", columnName, e);
		    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a módosítások elvégzése közben. Hibásan megadott paraméterek.");
		}
	}
	
	@GetMapping("/getMutableParameters/{activeFileId}/{rowNumber}")
	public ResponseEntity<?> getMutableParameters(@PathVariable int activeFileId, @PathVariable int rowNumber) {
		try {
			log.info("activeFileId: {}", activeFileId);
			log.info("rowNumber: {}", rowNumber);
			
			MutableParametersDTO fetchedMutableParams = modificationService.getMutableParametersForRow(activeFileId, rowNumber);
			return ResponseEntity.ok(fetchedMutableParams);
			
		} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
		} catch (IllegalArgumentException e) {
			log.error("Required parameters are missing", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a paraméterek lekérdezése közben. Hibásan megadott paraméterek.");
		}
	}
	
	@PutMapping("/setMutableParameters/{activeFileId}/{rowNumber}")
	public ResponseEntity<String> modifyMutableParams(@PathVariable int activeFileId,
														@PathVariable int rowNumber,
														@RequestBody MutableParametersDTO parameters) {
		
		try {
			log.info("activeFileId, rowNumber: {}", activeFileId, rowNumber);
			log.info("MutableParams: {}", parameters);
			
			modificationService.setMutableParametersForRow(activeFileId, rowNumber, parameters);
			
			return ResponseEntity.ok("Mutable Parameters modified and saved into the database");
		} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
		} catch (IllegalArgumentException e) {
			log.error("Required parameters are missing", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a paraméterek módosítása közben. Hibásan megadott paraméterek.");
		}
	}
	
	@PostMapping("/deleteRowsColumns/{activeFileId}")
	public ResponseEntity<String> deleteRowsColumns(@PathVariable int activeFileId, @RequestBody Map<String, List<Object>> data) {
		List<Object> rawSelectedRows = data.get("selectedRows");
	    List<Object> rawSelectedColumns = data.get("selectedColumns");

	    // Convert Object list to Integer list (handling null value)
	    List<Integer> selectedRows = null;
	    try {
		    selectedRows = rawSelectedRows != null ?
		            rawSelectedRows.stream()
		                    .map(obj -> Integer.parseInt(obj.toString()))
		                    .collect(Collectors.toList()) :
		            Collections.emptyList();
	    } catch (NumberFormatException e) {
	    	log.error("Error during parsing objects to integers", e);
	    }

	    // Convert Object list to String list (handling null value)
	    List<String> selectedColumns = rawSelectedColumns != null ?
	            rawSelectedColumns.stream()
	                    .map(Object::toString)
	                    .collect(Collectors.toList()) :
	            Collections.emptyList();
	    
	    log.info("selectedRows: {}", selectedRows);
	    log.info("selectedColumns: {}", selectedColumns);
	    
	    try {
	    	modificationService.deleteRowsColumns(activeFileId, selectedRows, selectedColumns);
	    	return ResponseEntity.ok("Deletion completed and database modified");
	    } catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
		} catch (IllegalArgumentException e) {
			log.error("Required parameters are missing", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a törlés elvégzése közben. Hibásan megadott paraméterek.");
		} catch (TransactionException e) {
			log.error("Transaction error occurred", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a törlés elvégzése közben.");
		}
	    
	    
	}
	
	

}
