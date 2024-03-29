package noise.road.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import noise.road.dto.ConstantParametersDTO;
import noise.road.dto.DbfDataDTO;
import noise.road.dto.MutableParametersDTO;
import noise.road.entity.Results;
import noise.road.repository.ResultsRepository;
import noise.road.service.calculationLogic.CalculationLogic;
import noise.road.service.calculationLogic.CalculationResults;
import noise.road.service.calculationLogic.DataService;

@Service
@Slf4j
public class CalculationsService {

	@Autowired
	private DataService dataService;
	
	@Autowired
	private ResultsRepository resultsRepository;
	
	private List<DbfDataDTO> dbfDataDTOList;
    private ConstantParametersDTO constantParameters;
    private List<MutableParametersDTO> mutableParameters;
    private List<CalculationResults> resultsList;
    private Double distanceToCalculate;
	
    public void fetchData(int fileId) {

    		dbfDataDTOList = new ArrayList<>();
    		dbfDataDTOList = dataService.fetchDbfData(fileId);
  //  		log.info("dbfDataDTOList: {}", dbfDataDTOList);

            constantParameters = dataService.fetchConstantParameters();
    //        log.info("constantParameters: {}", constantParameters);

    		mutableParameters = new ArrayList<>();
    		mutableParameters = dataService.fetchMutableParameters(fileId);
  //  		log.info("mutableParameters: {}", mutableParameters);

    		resultsList = getAllResults(fileId);
    //		log.info("resultsList: {}", resultsList);

    }
    
    public void calculateAll(int fileId) throws DataAccessException, IllegalArgumentException {
    	fetchData(fileId);
    	
    	List<Results> resultsEntitiesToUpdate = resultsRepository.findByFileId(fileId);
    	List<Results> resultEntitiesList = new ArrayList<>();
    	
    	for (int i = 0; i < resultsList.size() && i < resultsEntitiesToUpdate.size(); i++) {
			double[] laeqResult = resultsList.get(i).getLaeq();
			double[] lwResult = resultsList.get(i).getLw();
			double[] protectiveDistanceResult = resultsList.get(i).getProtectiveDistance();
			double[] impactAreaResult = resultsList.get(i).getImpactArea();
			
			Results results = resultsEntitiesToUpdate.get(i);
			
			results.setLaeqDay(laeqResult[0]);
			results.setLaeqNight(laeqResult[1]);
			results.setLwDay(lwResult[0]);
			results.setLwNight(lwResult[1]);
			results.setProtectiveDistanceDay(protectiveDistanceResult[0]);
			results.setProtectiveDistanceNight(protectiveDistanceResult[1]);
			results.setImpactAreaDay(impactAreaResult[0]);
			results.setImpactAreaNight(impactAreaResult[1]);
			
			resultEntitiesList.add(results);
		}
    	resultsRepository.saveAll(resultEntitiesList);	
    	
    }
    
    public void calculateLAeq(int fileId) throws DataAccessException, IllegalArgumentException {
		fetchData(fileId);

		List<Results> resultsEntitiesToUpdate = resultsRepository.findByFileId(fileId);
    	List<Results> resultEntitiesList = new ArrayList<>();

		for (int i = 0; i < resultsList.size() && i < resultsEntitiesToUpdate.size(); i++) {
			double[] laeqResult = resultsList.get(i).getLaeq();
			
			Results results = resultsEntitiesToUpdate.get(i);
			
			results.setLaeqDay(laeqResult[0]);
			results.setLaeqNight(laeqResult[1]);
			
			resultEntitiesList.add(results);
		}
		resultsRepository.saveAll(resultEntitiesList);		
	}
    
    public void calculateLw(int fileId) throws DataAccessException, IllegalArgumentException {
		fetchData(fileId);
		
		List<Results> resultsEntitiesToUpdate = resultsRepository.findByFileId(fileId);
    	List<Results> resultEntitiesList = new ArrayList<>();
		 
    	for (int i = 0; i < resultsList.size() && i < resultsEntitiesToUpdate.size(); i++) {
			double[] lwResult = resultsList.get(i).getLw();
			
			Results results = resultsEntitiesToUpdate.get(i);
			
			results.setLwDay(lwResult[0]);
			results.setLwNight(lwResult[1]);
			
			resultEntitiesList.add(results);
		}
		resultsRepository.saveAll(resultEntitiesList);		
	}
    
    public void calculateProtectiveDistance(int fileId) throws DataAccessException, IllegalArgumentException {
		fetchData(fileId);
		
		List<Results> resultsEntitiesToUpdate = resultsRepository.findByFileId(fileId);
    	List<Results> resultEntitiesList = new ArrayList<>();

    	for (int i = 0; i < resultsList.size() && i < resultsEntitiesToUpdate.size(); i++) {
			double[] protectiveDistanceResult = resultsList.get(i).getProtectiveDistance();
			
			Results results = resultsEntitiesToUpdate.get(i);
			
			results.setProtectiveDistanceDay(protectiveDistanceResult[0]);
			results.setProtectiveDistanceNight(protectiveDistanceResult[1]);
			
			resultEntitiesList.add(results);
		}
		resultsRepository.saveAll(resultEntitiesList);	
	}
    
    public void calculateImpactArea(int fileId) throws DataAccessException, IllegalArgumentException {
		fetchData(fileId);
		
		List<Results> resultsEntitiesToUpdate = resultsRepository.findByFileId(fileId);
    	List<Results> resultEntitiesList = new ArrayList<>();

    	for (int i = 0; i < resultsList.size() && i < resultsEntitiesToUpdate.size(); i++) {
			double[] impactAreaResult = resultsList.get(i).getImpactArea();
			
			Results results = resultsEntitiesToUpdate.get(i);
			
			results.setImpactAreaDay(impactAreaResult[0]);
			results.setImpactAreaNight(impactAreaResult[1]);
			
			resultEntitiesList.add(results);
		}
		resultsRepository.saveAll(resultEntitiesList);		
	}
    
    public void calculateNoiseAtGivenDistance(int fileId, double distance) throws DataAccessException, IllegalArgumentException {
		this.distanceToCalculate = distance;
		fetchData(fileId);
		
		List<Results> resultsEntitiesToUpdate = resultsRepository.findByFileId(fileId);
    	List<Results> resultEntitiesList = new ArrayList<>();
		
    	for (int i = 0; i < resultsList.size() && i < resultsEntitiesToUpdate.size(); i++) {
			double[] noiseAtGivenDistanceResult = resultsList.get(i).getNoiseAtGivenDistance();

			Results results = resultsEntitiesToUpdate.get(i);
			
			results.setNoiseAtGivenDistanceDay(noiseAtGivenDistanceResult[0]);
			results.setNoiseAtGivenDistanceNight(noiseAtGivenDistanceResult[1]);
			
			resultEntitiesList.add(results);
		}
		resultsRepository.saveAll(resultEntitiesList);
	}
    
    public void resetFetchedData() {
        dbfDataDTOList = null;
        constantParameters = null;
        mutableParameters = null;
    }
    
    private List<CalculationResults> getAllResults(int fileId) {
    	
		resultsList = new ArrayList<>();
		
    	int dataSize = Math.min(dbfDataDTOList.size(), mutableParameters.size());

        for (int i = 0; i < dataSize; i++) {
        	DbfDataDTO dbfDataDTO = dbfDataDTOList.get(i);
        	MutableParametersDTO mutableParam = mutableParameters.get(i);
        	ConstantParametersDTO constantParam = constantParameters;
        	
        	CalculationLogic calculationLogic = new CalculationLogic();
        	CalculationResults results = calculationLogic.perfromCalculations(dbfDataDTO, mutableParam, constantParam, distanceToCalculate);
        	
        	resultsList.add(results);
        }
        distanceToCalculate = null;
        
        return resultsList;
            
	}

}
