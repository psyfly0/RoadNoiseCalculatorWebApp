package noise.road.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import noise.road.repository.ConstantParametersRepository;
import noise.road.repository.DbfDataRepository;
import noise.road.repository.MutableParametersRepository;
import noise.road.repository.ResultsRepository;
import noise.road.repository.ShapeGeometryRepository;
import noise.road.repository.UserFileCounterRepository;

@Service
@Slf4j
public class UserDataCleanupService {

	@Autowired
    private DbfDataRepository dbfDataRepository;
	
	@Autowired
    private ResultsRepository resultsRepository;
	
	@Autowired
    private ConstantParametersRepository constantParamsRepository;
	
	@Autowired
    private MutableParametersRepository mutableParamsRepository;
	
	@Autowired
    private ShapeGeometryRepository shpGeometryRepository;
	
	@Autowired
	private UserFileCounterRepository userFileCounterRepsository;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cleanupUserData(String username) {
		
		deleteData(username);
        log.info("data deleted for user: {}", username);
    }
    
	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteGuestSchema(String username) {
        dropSchema(username);
        log.info("data deleted for guest: {}", username);
    }
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void deleteGuestData(String username) {
		
		deleteData(username);
		
		resultsRepository.deleteAllData();
    	mutableParamsRepository.deleteAllData();
    	shpGeometryRepository.deleteAllData();
    	constantParamsRepository.deleteAllData();
        dbfDataRepository.deleteAllData();
        log.info("data deleted for guest: {}", username);
	}
	
	private void deleteData(String username) {	
		
		userFileCounterRepsository.deleteAllData();
		userFileCounterRepsository.resetIdSequence();
		
    	resultsRepository.deleteAllData();
    	resultsRepository.resetIdSequence();
    	
    	mutableParamsRepository.deleteAllData();
    	mutableParamsRepository.resetIdSequence();
    	
    	shpGeometryRepository.deleteAllData();
    	shpGeometryRepository.resetIdSequence();
    	
    	constantParamsRepository.deleteAllData();
    	constantParamsRepository.resetIdSequence();
    	
        dbfDataRepository.deleteAllData();
        dbfDataRepository.resetIdSequence();

	}
    
    private void dropSchema(String schemaName) {
    	String dropSchemaSql = "DROP SCHEMA IF EXISTS \"" + schemaName + "\" CASCADE";
        try {
            jdbcTemplate.execute(dropSchemaSql);
            log.info("Schema dropped successfully: " + schemaName);
        } catch (Exception e) {
            log.error("Error dropping schema: " + schemaName, e);
        }
    }
}
