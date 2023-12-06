package noise.road.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import noise.road.dto.DbfDataPreprocessDTO;
import noise.road.dto.MutableParametersDTO;
import noise.road.dto.SaveDbfRequest;
import noise.road.dto.ShapeDataDTO;
import noise.road.service.ConstantParametersService;
import noise.road.service.DbfDataService;
import noise.road.service.FileReadService;

@RestController
@RequestMapping("/console")
@Slf4j
public class FileReadSaveController {
	
	private boolean constantsAlreadySaved = false;
	
	@Autowired
	private FileReadService fileReadService;
	
	@Autowired
	private DbfDataService dbfDataService;
	
	@Autowired
	private ConstantParametersService constantParametersService;

    @PostMapping("/fileLoad")
    public List<Map<String, Object>> fileLoad(@RequestParam("zipFile") MultipartFile zipFile) throws IOException {
        ShapeDataDTO shapeData = fileReadService.readShapefileFromZip(zipFile);
        List<Map<String, Object>> attributeData = shapeData.getAttributeData();
        return attributeData;     
    }
    
    @PostMapping("/saveToDatabase")
    public ResponseEntity<String> saveToDatabase(@RequestBody SaveDbfRequest saveDbfRequest) {
    	if (!constantsAlreadySaved) {
    		constantParametersService.insertParametersToDatabase();
    		constantsAlreadySaved = true;
    	}
    	
    	String fileName = saveDbfRequest.getFileName();
        List<DbfDataPreprocessDTO> requestBody = saveDbfRequest.getMappedData();
        
    	dbfDataService.saveDbfData(requestBody, fileName);
     
        return ResponseEntity.ok("Data saved successfully to the database");
       
    }
    
    @PostMapping("/saveMutableParameters")
    public ResponseEntity<String> saveMutableParametersToDatabase(@RequestBody MutableParametersDTO parameters) {
    	log.info("Mutable parameters: {}", parameters);
    	return ResponseEntity.ok("Mutable parameters saved successfully to the database");
    }

}
