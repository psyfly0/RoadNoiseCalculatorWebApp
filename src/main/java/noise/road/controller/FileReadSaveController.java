package noise.road.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> fileLoad(@RequestParam("zipFile") MultipartFile zipFile) {
    	try {
            shapeData = fileReadService.readShapefileFromZip(zipFile);
            List<Map<String, Object>> attributeData = shapeData.getAttributeData();
            return ResponseEntity.ok(attributeData);
        } catch (IOException e) {
            log.error("Error reading shapefile from ZIP", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba a .zip olvasása közben.");
        } catch (IllegalArgumentException e) {
            log.error("Required files are missing in the ZIP", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A .zip nem tartalmaz minden szükséges fájlt.");
        }    
    }
    
    @PostMapping("/saveToDatabase")
    public ResponseEntity<String> saveToDatabase(@RequestBody SaveDbfRequest saveDbfRequest) {
    	try {
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
	        
    	} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
    	} catch (IOException e) {
    		log.error("IO error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba a fájl mentése közben.");
    	} catch (IllegalArgumentException e) {
    		log.error("Required parameters are missing", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a fájl mentése közben. Hibásan megadott oszlopnevek és/vagy paraméterek.");
    	}
       
    }
    
    @PostMapping("/saveMutableParameters")
    public ResponseEntity<String> saveMutableParametersToDatabase(@RequestBody MutableParametersDTO parameters) {
    	log.info("Mutable parameters: {}", parameters);
    	try {
    		
	    	mutableParametersService.saveInitialMutableParameters(parameters, uploadedDataLength);	    	
	    	return ResponseEntity.ok("Mutable parameters saved successfully to the database");
	    	
    	} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
    	} catch (IOException e) {
    		log.error("IO error occurred", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba a paraméterek mentése közben.");
    	} catch (IllegalArgumentException e) {
    		log.error("Required parameters are missing", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hiba a paraméterek mentése közben. Hibásan megadott paraméterek.");
    	}
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
		

		generatedZipFile.delete();


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + ".zip\"")
                .body(resource);
    }
    
    @PostMapping("/saveProtectiveDistance/{activeFileId}/{fileName}")
    public ResponseEntity<Resource> saveProtectiveDistance(@PathVariable int activeFileId, @PathVariable String fileName) {
    	String saveName = "vedotav";
    	String saveFolderName = "PROTECTIVE";
    	
    	File generatedZipFile = fileSaveService.saveProtectiveAndImpactAreaDistance(activeFileId, fileName, saveName, saveFolderName);
    	
    	// Provide the generated .zip file for download
        Path path = Paths.get(generatedZipFile.getAbsolutePath());
        ByteArrayResource resource = null;

        try {
			resource = new ByteArrayResource(Files.readAllBytes(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        generatedZipFile.delete();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "_" + saveName  + ".zip\"")
                .body(resource);
    }
    
    @PostMapping("/saveImpactAreaDistance/{activeFileId}/{fileName}")
    public ResponseEntity<Resource> saveImpactAreaDistance(@PathVariable int activeFileId, @PathVariable String fileName) {
    	String saveName = "hatasTer";
    	String saveFolderName = "IMPACTAREA";
    	
    	File generatedZipFile = fileSaveService.saveProtectiveAndImpactAreaDistance(activeFileId, fileName, saveName, saveFolderName);
    	
    	// Provide the generated .zip file for download
        Path path = Paths.get(generatedZipFile.getAbsolutePath());
        ByteArrayResource resource = null;

        try {
			resource = new ByteArrayResource(Files.readAllBytes(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        generatedZipFile.delete();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "_" + saveName  + ".zip\"")
                .body(resource);
    }

}
