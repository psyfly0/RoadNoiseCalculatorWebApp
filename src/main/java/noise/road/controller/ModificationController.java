package noise.road.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
		
		log.info("activeFileId, row, columnName, updatedCellValue: {}", activeFileId, row, columnName, updatedCellValue);
		modificationService.modifyCellValue(activeFileId, row, columnName, updatedCellValue);
		
		return ResponseEntity.ok("Cell modification performed and saved it to the database");
	}
	
	@GetMapping("/getMutableParameters/{activeFileId}/{rowNumber}")
	public MutableParametersDTO getMutableParameters(@PathVariable int activeFileId, @PathVariable int rowNumber) {
		log.info("activeFileId: {}", activeFileId);
		log.info("rowNumber: {}", rowNumber);
		
		return modificationService.getMutableParametersForRow(activeFileId, rowNumber);
	}
	
	@PutMapping("/setMutableParameters/{activeFileId}/{rowNumber}")
	public ResponseEntity<String> modifyMutableParams(@PathVariable int activeFileId,
														@PathVariable int rowNumber,
														@RequestBody MutableParametersDTO parameters) {
		
		log.info("activeFileId, rowNumber: {}", activeFileId, rowNumber);
		log.info("MutableParams: {}", parameters);
		
		modificationService.setMutableParametersForRow(activeFileId, rowNumber, parameters);
		
		return ResponseEntity.ok("Mutable Parameters modified and saved into the database");
	}

}
