package noise.road.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.opengis.referencing.FactoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
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
    public ResponseEntity<String> saveToDatabase(@RequestBody SaveDbfRequest saveDbfRequest, Authentication auth) {
    	try {
    		constantParametersService.insertParametersToDatabase();
    		
	    	String fileName = saveDbfRequest.getFileName();
	        List<DbfDataPreprocessDTO> requestBody = saveDbfRequest.getMappedData();
	        List<Geometry> geometries = shapeData.getGeometries();
	        String username = auth.getName();
	        
	        log.info("requestBody: {}", requestBody);
	        
	        saveDisplayService.saveData(requestBody, fileName, geometries, username);
	    	
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
    public ResponseEntity<String> saveMutableParametersToDatabase(@RequestBody MutableParametersDTO parameters, Authentication auth) {
    	log.info("Mutable parameters: {}", parameters);
    	try {
    		String username = auth.getName();
	    	mutableParametersService.saveInitialMutableParameters(parameters, uploadedDataLength, username);	    	
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
    public ResponseEntity<?> saveFile(@PathVariable int activeFileId, 
												@PathVariable String fileName, 
												@RequestBody List<String> columnNames) {
    	
    	log.info("fileName: {}", fileName);
    	log.info("Received column names: {}", columnNames);
    	
    	File generatedZipFile = null;
    	
    	try {
    		generatedZipFile = fileSaveService.saveShpFile(activeFileId, fileName, columnNames);   	
    	} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
    	} catch (FactoryException e) {
    		log.error("CRS parsing error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba a koordináta átváltásban.");
    	} catch (IOException e) {
    		log.error("Error during reading/writing files", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba a fájlok olvasása/írása közben.");
    	} catch (ParseException e) {
    		log.error("Error parsing WKT", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba a geometria olvasása közben.");
    	} catch (IllegalArgumentException e) {
    		log.error("Required parameters are missing", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hibásan megadott paraméterek.");
    	}
	
    	// Provide the generated .zip file for download
        Path path = Paths.get(generatedZipFile.getAbsolutePath());
        ByteArrayResource resource = null;
		try {
			resource = new ByteArrayResource(Files.readAllBytes(path));
		} catch (IOException e) {
			log.error("Error during fetching the generated .zip file", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az elkészült .zip elérése során.");
		}
		
		generatedZipFile.delete();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + ".zip\"")
                .body(resource);
    }
    
    @PostMapping("/saveProtectiveDistance/{activeFileId}/{fileName}")
    public ResponseEntity<?> saveProtectiveDistance(@PathVariable int activeFileId, @PathVariable String fileName) {
    	String saveName = "vedotav";
    	String saveFolderName = "PROTECTIVE";
    	
    	File generatedZipFile = null;
    	
    	try {
    		generatedZipFile = fileSaveService.saveProtectiveAndImpactAreaDistance(activeFileId, fileName, saveName, saveFolderName);
    	} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
    	} catch (FactoryException e) {
    		log.error("CRS parsing error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba a koordináta átváltásban.");
    	} catch (IOException e) {
    		log.error("Error during reading/writing files", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba a fájlok olvasása/írása közben.");
    	} catch (ParseException e) {
    		log.error("Error parsing WKT", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba a geometria olvasása közben.");
    	} catch (IllegalArgumentException e) {
    		log.error("Required parameters are missing", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hibásan megadott paraméterek.");
    	}   	
    	
    	// Provide the generated .zip file for download
        Path path = Paths.get(generatedZipFile.getAbsolutePath());
        ByteArrayResource resource = null;

        try {
			resource = new ByteArrayResource(Files.readAllBytes(path));
		} catch (IOException e) {
			log.error("Error during fetching the generated .zip file", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az elkészült .zip elérése során.");
		}
        
        generatedZipFile.delete();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "_" + saveName  + ".zip\"")
                .body(resource);
    }
    
    @PostMapping("/saveImpactAreaDistance/{activeFileId}/{fileName}")
    public ResponseEntity<?> saveImpactAreaDistance(@PathVariable int activeFileId, @PathVariable String fileName) {
    	String saveName = "hatasTer";
    	String saveFolderName = "IMPACTAREA";
    	
    	File generatedZipFile = null;
    	try {
    		generatedZipFile = fileSaveService.saveProtectiveAndImpactAreaDistance(activeFileId, fileName, saveName, saveFolderName);
    	} catch (DataAccessException e) {
    		log.error("Database access error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az adatbázis elérésében.");
    	} catch (FactoryException e) {
    		log.error("CRS parsing error occurred", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba a koordináta átváltásban.");
    	} catch (IOException e) {
    		log.error("Error during reading/writing files", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba a fájlok olvasása/írása közben.");
    	} catch (ParseException e) {
    		log.error("Error parsing WKT", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba a geometria olvasása közben.");
    	} catch (IllegalArgumentException e) {
    		log.error("Required parameters are missing", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Hibásan megadott paraméterek.");
    	} 
    		
    	// Provide the generated .zip file for download
        Path path = Paths.get(generatedZipFile.getAbsolutePath());
        ByteArrayResource resource = null;

        try {
			resource = new ByteArrayResource(Files.readAllBytes(path));
		} catch (IOException e) {
			log.error("Error during fetching the generated .zip file", e);
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Hiba az elkészült .zip elérése során.");
		}
        
        generatedZipFile.delete();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "_" + saveName  + ".zip\"")
                .body(resource);
    }

}
