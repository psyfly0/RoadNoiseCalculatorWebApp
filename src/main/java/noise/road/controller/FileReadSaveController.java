package noise.road.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import noise.road.dto.DbfDataDTO;
import noise.road.dto.DbfDataPreprocessDTO;
import noise.road.dto.MutableParametersDTO;
import noise.road.dto.SaveDbfRequest;
import noise.road.dto.ShapeDataDTO;
import noise.road.service.ConstantParametersService;
import noise.road.service.SaveDisplayService;
import noise.road.service.FileReadService;
import noise.road.service.FileSaveService;
import noise.road.service.MutableParametersService;

@RestController
@RequestMapping("/console")
@Slf4j
public class FileReadSaveController {
	
	private ShapeDataDTO shapeData;
	private int uploadedDataLength = 0;
	private boolean constantsAlreadySaved = false;
	
	@Autowired
	private FileReadService fileReadService;
	
	@Autowired
	private SaveDisplayService saveDisplayService;
	
	@Autowired
	private ConstantParametersService constantParametersService;
	
	@Autowired
	private MutableParametersService mutableParametersService;
	
	@Autowired
	private FileSaveService fileSaveService;

    @PostMapping("/fileLoad")
    public List<Map<String, Object>> fileLoad(@RequestParam("zipFile") MultipartFile zipFile) throws IOException {
        shapeData = fileReadService.readShapefileFromZip(zipFile);
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
        List<Geometry> geometries = shapeData.getGeometries();
        
        log.info("requestBody: {}", requestBody);
        
        saveDisplayService.saveData(requestBody, fileName, geometries);
    	
    	// fetch the length of data for mutable parameters
    	uploadedDataLength = requestBody.size();
     
        return ResponseEntity.ok("Data saved successfully to the database");
       
    }
    
    @PostMapping("/saveMutableParameters")
    public ResponseEntity<String> saveMutableParametersToDatabase(@RequestBody MutableParametersDTO parameters) {
    	log.info("Mutable parameters: {}", parameters);
    	mutableParametersService.saveInitialMutableParameters(parameters, uploadedDataLength);
    	
    	return ResponseEntity.ok("Mutable parameters saved successfully to the database");
    }
    
    @PostMapping("/saveFile/{activeFileId}/{fileName}")
    public ResponseEntity<Resource> saveFile(@PathVariable int activeFileId, 
												@PathVariable String fileName, 
												@RequestBody List<String> columnNames) {
    	
    	log.info("fileName: {}", fileName);
    	log.info("Received column names: {}", columnNames);
  
    	File generatedZipFile = fileSaveService.saveShpFile(activeFileId, fileName, columnNames);
    	
    	// Provide the generated .zip file for download
        Path path = Paths.get(generatedZipFile.getAbsolutePath());
        ByteArrayResource resource = null;
		try {
			resource = new ByteArrayResource(Files.readAllBytes(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + ".zip\"")
                .body(resource);
    }
    
    @PostMapping("/saveProtectiveDistance/{activeFileId}/{fileName}")
    public ResponseEntity<Resource> saveProtectiveDistance(@PathVariable int activeFileId, @PathVariable String fileName) {
    	
    	File generatedZipFile = fileSaveService.saveProtectiveDistance(activeFileId, fileName);
    	
    	// Provide the generated .zip file for download
        Path path = Paths.get(generatedZipFile.getAbsolutePath());
        ByteArrayResource resource = null;

        try {
			resource = new ByteArrayResource(Files.readAllBytes(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "_vedotav" + ".zip\"")
                .body(resource);
    }

}
