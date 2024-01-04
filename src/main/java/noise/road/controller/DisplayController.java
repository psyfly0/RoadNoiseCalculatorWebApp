package noise.road.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import noise.road.dto.DbfDataDTO;

import noise.road.service.SaveDisplayService;

@RestController
@RequestMapping("/console")
@Slf4j
public class DisplayController {
	
	@Autowired
    private SaveDisplayService saveDisplayService;
	
	@GetMapping("/displayData")
	public Map<Integer, List<DbfDataDTO>> displayAllData(Authentication authentication) {
		// Check the user's role and serve different content based on the role
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_GUEST"))) {
        	
            // Serve content for ROLE_GUEST
        	Map<Integer, List<DbfDataDTO>> data = saveDisplayService.getAll();
			log.info("All files for GUEST: {}", data);
			return data;
        } else {
        	// other roles
			try {
				Map<Integer, List<DbfDataDTO>> data = saveDisplayService.getAll();
				log.info("All files: {}", data);
				return data;
			} catch (DataAccessException e) {
	            log.error("Database access error occurred", e);
	        } catch (NullPointerException e) {
	            log.error("Null pointer exception occurred", e);
	        } catch (MappingException e) {
	            log.error("Mapping exception occurred", e);
	        } catch (Exception e) {
	            log.error("Unexpected error occurred", e);
	        }
			return Collections.emptyMap();
        }
	}
	
}
