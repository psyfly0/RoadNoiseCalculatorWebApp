package noise.road.service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import noise.road.dto.ConstantParametersDTO;
import noise.road.dto.DbfDataDTO;
import noise.road.dto.MutableParametersDTO;
import noise.road.entity.ConstantParameters;
import noise.road.entity.DbfData;
import noise.road.entity.MutableParameters;
import noise.road.repository.ConstantParametersRepository;
import noise.road.repository.DbfDataRepository;
import noise.road.repository.MutableParametersRepository;

@Service
@Slf4j
public class CalculationsService {

	@Autowired
	private DataService dataService;
	
	@Autowired
	private DbfDataRepository dbfDataRepository;
	
	private List<DbfDataDTO> dbfDataDTOList;
    private ConstantParametersDTO constantParameters;
    private List<MutableParametersDTO> mutableParameters;
    private List<CalculationResults> resultsList;
	
    public void fetchData(int fileId) {
    	boolean isFetchedDbf = false;
    	boolean isFetchedConstant = false;
    	boolean isFetchedMutable = false;
    	
    	if (dbfDataDTOList == null || dbfDataDTOList.stream().noneMatch(data -> data.getFile_id() == fileId)) {
    		dbfDataDTOList = new ArrayList<>();
    		dbfDataDTOList = dataService.fetchDbfData(fileId);
    		isFetchedDbf = true;
    		log.info("dbfDataDTOList: {}", dbfDataDTOList);
    	}
    	
    	if (constantParameters == null) {
            constantParameters = dataService.fetchConstantParameters();
            isFetchedConstant = true;
            log.info("constantParameters: {}", constantParameters);
        }
    	
    	if (mutableParameters == null || mutableParameters.stream().noneMatch(data -> data.getFile_id() == fileId)) {
    		mutableParameters = new ArrayList<>();
    		mutableParameters = dataService.fetchMutableParameters(fileId);
    		isFetchedMutable = true;
    		log.info("mutableParameters: {}", mutableParameters);
    	}
    	
    	if (isFetchedDbf || isFetchedConstant || isFetchedMutable) {
    		resultsList = getAllResults(fileId);
    		log.info("resultsList: {}", resultsList);
    	}
    }
    
    public void calculateAll(int fileId) {
    	fetchData(fileId);
    	
    	List<DbfData> dbfDataList = new ArrayList<>();
    	
    	for (CalculationResults results : resultsList) {
    		double[] laeqResult = results.getLaeq();
            double[] lwResult = results.getLw();
            double[] protectiveDistanceResult = results.getProtectiveDistance();
            double[] impactAreaResult = results.getImpactArea();
        	
        	DbfData dbfData = new DbfData();
        	dbfData.setLaeqDay(laeqResult[0]);
        	dbfData.setLaeqNight(laeqResult[1]);
        	dbfData.setLwDay(lwResult[0]);
        	dbfData.setLwNight(lwResult[1]);
        	dbfData.setProtectiveDistanceDay(protectiveDistanceResult[0]);
        	dbfData.setProtectiveDistanceNight(protectiveDistanceResult[1]);
        	dbfData.setImpactAreaDay(impactAreaResult[0]);
        	dbfData.setImpactAreaNight(impactAreaResult[1]);
			
			dbfDataList.add(dbfData);
		}
    	log.info("All calculations dbfDataList: {}", dbfDataList);
    	dbfDataRepository.saveAll(dbfDataList);	
    	
    }
    
	public void calculateLAeq(int fileId) {
		fetchData(fileId);
		Instant start = Instant.now();
		 List<DbfData> dbfDataEntitiesToUpdate = dbfDataRepository.findByFileId(fileId);
		 List<DbfData> dbfDataList = new ArrayList<>();

		for (int i = 0; i < resultsList.size() && i < dbfDataEntitiesToUpdate.size(); i++) {
			double[] laeqResult = resultsList.get(i).getLaeq();
			//DbfData dbfData = new DbfData();
			
			DbfData dbfData = dbfDataEntitiesToUpdate.get(i);
			
			dbfData.setLaeqDay(laeqResult[0]);
			dbfData.setLaeqNight(laeqResult[1]);
			
			dbfDataList.add(dbfData);
		}
		log.info("LAeq dbfDataList: {}", dbfDataList);
		dbfDataRepository.saveAll(dbfDataList);	
		Instant end = Instant.now();
		Duration duration = Duration.between(start, end);
		log.info("Duration time of mapping dto to entites and save it to db: {}", duration);
	}
	
	public void calculateLw(int fileId) {
		fetchData(fileId);
		
		List<DbfData> dbfDataList = new ArrayList<>();

		for (CalculationResults results : resultsList) {
			double[] lwResult = results.getLw();
			DbfData dbfData = new DbfData();
			
			dbfData.setLwDay(lwResult[0]);
			dbfData.setLwNight(lwResult[1]);
			
			dbfDataList.add(dbfData);
		}
		log.info("LWeq dbfDataList: {}", dbfDataList);
		dbfDataRepository.saveAll(dbfDataList);		
	}
	
	public void calculateProtectiveDistance(int fileId) {
		fetchData(fileId);
		
		List<DbfData> dbfDataList = new ArrayList<>();

		for (CalculationResults results : resultsList) {
			double[] protectiveDistanceResult = results.getProtectiveDistance();
			DbfData dbfData = new DbfData();
			
			dbfData.setProtectiveDistanceDay(protectiveDistanceResult[0]);
        	dbfData.setProtectiveDistanceNight(protectiveDistanceResult[1]);
			
			dbfDataList.add(dbfData);
		}
		log.info("ProtectiveDistance dbfDataList: {}", dbfDataList);
		dbfDataRepository.saveAll(dbfDataList);		
	}
	
	public void calculateImpactArea(int fileId) {
		fetchData(fileId);
		
		List<DbfData> dbfDataList = new ArrayList<>();

		for (CalculationResults results : resultsList) {
			double[] impactAreaResult = results.getImpactArea();
			DbfData dbfData = new DbfData();
			
			dbfData.setImpactAreaDay(impactAreaResult[0]);
        	dbfData.setImpactAreaNight(impactAreaResult[1]);
			
			dbfDataList.add(dbfData);
		}
		log.info("ImpactArea dbfDataList: {}", dbfDataList);
		dbfDataRepository.saveAll(dbfDataList);		
	}
	
	private List<CalculationResults> getAllResults(int fileId) {
    	
		resultsList = new ArrayList<>();
		
    	int dataSize = Math.min(dbfDataDTOList.size(), mutableParameters.size());

        for (int i = 0; i < dataSize; i++) {
        	DbfDataDTO dbfDataDTO = dbfDataDTOList.get(i);
        	MutableParametersDTO mutableParam = mutableParameters.get(i);
        	ConstantParametersDTO constantParam = constantParameters;
        	
        	CalculationLogic calculationLogic = new CalculationLogic();
        	CalculationResults results = calculationLogic.perfromCalculations(dbfDataDTO, mutableParam, constantParam);
        	
        	resultsList.add(results);
        }
        
            return resultsList;
            
	}
	
	
	
	public void resetFetchedData() {
        dbfDataDTOList = null;
        constantParameters = null;
        mutableParameters = null;
    }

}
