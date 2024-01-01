package noise.road.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import noise.road.entity.Results;
import noise.road.repository.ResultsRepository;

@Service
@Slf4j
public class DifferenceCalculatorService {

	private final static Double CLOSED = -100.0;
	private final static Double NEW = 100.0;
	@Autowired
	private ResultsRepository resultsRepository;
	
	
	
	public void calculateDifferences(int activeFileId, int fileIdSecond, int fieldsToUpdateInDB) throws IllegalArgumentException, DataAccessException {
		List<Object[]> resultsFirstFile = resultsRepository.findLaeqAndIdentifierById(activeFileId);
		List<Object[]> resultsSecondFile = resultsRepository.findLaeqAndIdentifierById(fileIdSecond);
		
		Map<Integer, Object[]> secondFileResultsMap = new HashMap<>();
	    for (Object[] result : resultsSecondFile) {
	        Integer identifier = (Integer) result[2];
	        secondFileResultsMap.put(identifier, result);
	    }
		
		List<Results> resultsEntitiesToUpdate = resultsRepository.findByFileId(activeFileId);
    	List<Results> resultEntitiesList = new ArrayList<>();
		
    	Double differenceLaeqDay = 0.0;
    	Double differenceLaeqNight = 0.0;
		
    	for (int i = 0; i < resultsFirstFile.size(); i++) {
            Object[] result = resultsFirstFile.get(i);
			Double activeFileLaeqDay = (Double) result[0];
			Double activeFileLaeqNight = (Double) result[1];
			Integer activeFileIdentifier = (Integer) result[2];

			if (secondFileResultsMap.containsKey(activeFileIdentifier)) {
				Object[] correspondingSecondResult = secondFileResultsMap.get(activeFileIdentifier);
	            Double secondFileLaeqDay = (Double) correspondingSecondResult[0];
	            Double secondFileLaeqNight = (Double) correspondingSecondResult[1];
	            
	            if (activeFileLaeqDay == 0 && secondFileLaeqDay != 0) {
	            	differenceLaeqDay = CLOSED;
	            } else if (activeFileLaeqDay != 0 && secondFileLaeqDay == 0) {
	            	differenceLaeqDay = NEW;
	            } else {
	            	differenceLaeqDay = Math.round((activeFileLaeqDay - secondFileLaeqDay) * 10) / 10.0;
	            }
	            
	            if (activeFileLaeqNight == 0 && secondFileLaeqNight != 0) {
	            	differenceLaeqNight = CLOSED;
	            } else if (activeFileLaeqNight != 0 && secondFileLaeqNight == 0) {
	            	differenceLaeqNight = NEW;
	            } else {
	            	differenceLaeqNight = Math.round((activeFileLaeqNight - secondFileLaeqNight) * 10) / 10.0;
	            }
	            
	        } else {
	        	differenceLaeqDay = NEW;
	        	differenceLaeqNight = NEW;
	        }
	        
			if (i < resultsEntitiesToUpdate.size()) {
				Results resultsEntity = resultsEntitiesToUpdate.get(i);
				switch (fieldsToUpdateInDB) {
				case 0:
	                resultsEntity.setDifferenceDay0(differenceLaeqDay);
	                resultsEntity.setDifferenceNight0(differenceLaeqNight);
	                resultEntitiesList.add(resultsEntity);
	                break;
				case 1:
	                resultsEntity.setDifferenceDay1(differenceLaeqDay);
	                resultsEntity.setDifferenceNight1(differenceLaeqNight);
	                resultEntitiesList.add(resultsEntity);
	                break;
				case 2:
					resultsEntity.setDifferenceDay2(differenceLaeqDay);
	                resultsEntity.setDifferenceNight2(differenceLaeqNight);
	                resultEntitiesList.add(resultsEntity);
	                break;
                default:
                	log.info("Error during saving to DB");
                	break;
				}
            } else {
                log.warn("Index out of bounds for resultsEntitiesToUpdate at index {}", i);
            }   
		}
		log.info("Differences - resultEntitiesList: {}", resultEntitiesList);
    	resultsRepository.saveAll(resultEntitiesList);	
		
	}
}
