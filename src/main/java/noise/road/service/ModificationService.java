package noise.road.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import noise.road.dto.MutableParametersDTO;
import noise.road.entity.DbfData;
import noise.road.entity.MutableParameters;
import noise.road.entity.Results;
import noise.road.entity.ShapeGeometry;
import noise.road.repository.DbfDataRepository;
import noise.road.repository.MutableParametersRepository;
import noise.road.repository.ResultsRepository;
import noise.road.repository.ShapeGeometryRepository;

@Service
@Slf4j
public class ModificationService {

	@Autowired
	private DbfDataRepository dbfDataRepository;
	
	@Autowired
	private ResultsRepository resultsRepository;
	
	@Autowired
	private ShapeGeometryRepository shapeGeometryRepository;
	
	@Autowired
	private MutableParametersRepository mutableParamRepository;
	
	@Autowired
	private MutableParametersService mutableParamsService;
	
	@Autowired
    private ModelMapper modelMapper;
	
	@PersistenceContext
    private EntityManager entityManager;
	
	public void modifyCellValue(int activeFileId, int rowNumber, String columnName, Integer updatedCellValue) throws DataAccessException, IllegalArgumentException, NoSuchMethodException{

		int rowAdjusted = rowNumber + 1;
		DbfData dbfData = dbfDataRepository.findRow(activeFileId, rowAdjusted);
	
		 try {
	            // Capitalize the columnName to match the setter method convention
	            String capitalizedColumnName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);

	            // Get the corresponding setter method
	            Method setterMethod = DbfData.class.getMethod("set" + capitalizedColumnName, Integer.class);

	            // Invoke the setter method to set the updated cell value
	            setterMethod.invoke(dbfData, updatedCellValue);

	            // Save the updated entity to the database
	            dbfDataRepository.save(dbfData);

	        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
	            e.printStackTrace();
	        }	
	}
	
	public MutableParametersDTO getMutableParametersForRow(int activeFileId, int rowNumber) throws DataAccessException, IllegalArgumentException {
		
		int rowAdjusted = rowNumber + 1;		
		MutableParameters mutableParam = mutableParamRepository.findRow(activeFileId, rowAdjusted);
		
		MutableParametersDTO mutableParamDTO = modelMapper.map(mutableParam, MutableParametersDTO.class);

        return mutableParamDTO;
	}
	
	public void setMutableParametersForRow(int activeFileId, int rowNumber, MutableParametersDTO mutableParamsDTO) throws DataAccessException, IllegalArgumentException {
		int rowAdjusted = rowNumber + 1;
		
		MutableParameters mutableParam = mutableParamRepository.findRow(activeFileId, rowAdjusted);
		
		mutableParam.setLthDay(mutableParamsDTO.getLthDay());
		mutableParam.setLthNight(mutableParamsDTO.getLthNight());
		mutableParam.setRoadSurfaceRoughness(mutableParamsDTO.getRoadSurfaceRoughness());
		mutableParam.setReflection(mutableParamsDTO.getReflection());
		mutableParam.setSoundAbsorptionFactor(mutableParamsDTO.getSoundAbsorptionFactor());
		mutableParam.setAngleOfView(mutableParamsDTO.getAngleOfView());
		mutableParam.setTrafficType(mutableParamsDTO.getTrafficType());
		mutableParam.setSlopeElevation(mutableParamsDTO.getSlopeElevation());
		mutableParam.setParameterP_cat1(mutableParamsService.calculateCategoryIparameterP(mutableParamsDTO.getSlopeElevation(), mutableParamsDTO.getTrafficType()));
		mutableParam.setParameterP_cat2_3(mutableParamsService.calculateCategoryIIandIIIparameterP(mutableParamsDTO.getSlopeElevation(), mutableParamsDTO.getTrafficType()));
		
		mutableParamRepository.save(mutableParam);
		
	}
	
	@Transactional
	public void deleteRowsColumns(int activeFileId, List<Integer> rowNumbers, List<String> columnNames) throws DataAccessException, IllegalArgumentException, TransactionException {
		log.info("activeFileId: {}", activeFileId);
		log.info("rowNumber: {}", rowNumbers);
		log.info("columnNames: {}", columnNames);
		
		if (rowNumbers != null && !rowNumbers.isEmpty()) {			
			shapeGeometryRepository.deleteRowsByFileIdAndRowNumbers(activeFileId, rowNumbers);
			resultsRepository.deleteRowsByFileIdAndRowNumbers(activeFileId, rowNumbers);
			dbfDataRepository.deleteRowsByFileIdAndRowNumbers(activeFileId, rowNumbers);
        }

        if (columnNames != null && !columnNames.isEmpty()) {
        	if (columnNames != null && !columnNames.isEmpty()) {
                for (String columnName : columnNames) {
                    updateColumnAsDeleted(activeFileId, columnName);
                }
            }
        }
	}

	private void updateColumnAsDeleted(int activeFileId, String columnName) {
		String snakeCaseColumnName = camelToSnakeCase(columnName);
		String tableName = determineTable(columnName);
		
        String sql = "UPDATE " + tableName + " SET " + snakeCaseColumnName + " = null WHERE FILE_ID = :fileId";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("fileId", activeFileId);
        query.executeUpdate();
    }
	
	private String camelToSnakeCase(String columnName) {
	    String[] parts = columnName.split("(?=[A-Z\\d])");
	    return String.join("_", parts).toUpperCase();
	}
	
	private String determineTable(String columnName) {
		List<String> dbfDataTableColumns = new ArrayList<>(Arrays.asList(
				"identifier", "speed1", "speed2", "speed3", "acousticCatDay1", "acousticCatDay2", 
				"acousticCatDay3", "acousticCatNight1", "acousticCatNight2", "acousticCatNight3",
				"identifierR", "speed1R", "speed2R", "speed3R", "acousticCatDay1R", "acousticCatDay2R", 
				"acousticCatDay3R", "acousticCatNight1R", "acousticCatNight2R", "acousticCatNight3R"));
		
		List<String> resultsTableColumns = new ArrayList<>(Arrays.asList(
				"laeqDay", "laeqNight", "lwDay", "lwNight", "impactAreaDay", "impactAreaNight",
				"protectiveDistanceDay", "protectiveDistanceNight", "noiseAtGivenDistanceDay", 
				"noiseAtGivenDistanceNight", "differenceDay0", "differenceNight0", "differenceDay1", 
				"differenceNight1", "differenceDay2", "differenceNight2"));
		
		if (dbfDataTableColumns.contains(columnName)) {
			return "DBF_DATA";
		} else if (resultsTableColumns.contains(columnName)) {
			return "RESULTS";
		} else {
			return null;
		}
	}
	
	



	
	
	
	
}
