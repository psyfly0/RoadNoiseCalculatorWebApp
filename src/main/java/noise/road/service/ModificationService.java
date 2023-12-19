package noise.road.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import noise.road.dto.MutableParametersDTO;
import noise.road.entity.DbfData;
import noise.road.entity.MutableParameters;
import noise.road.repository.DbfDataRepository;
import noise.road.repository.MutableParametersRepository;

@Service
public class ModificationService {

	@Autowired
	private DbfDataRepository dbfDataRepository;
	
	@Autowired
	private MutableParametersRepository mutableParamRepository;
	
	@Autowired
	private MutableParametersService mutableParamsService;
	
	@Autowired
    private ModelMapper modelMapper;
	
	public void modifyCellValue(int activeFileId, int row, String columnName, Integer updatedCellValue) {

		int rowAdjusted = row + 1;
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
	
	public MutableParametersDTO getMutableParametersForRow(int activeFileId, int rowNumber) {
		
		int rowAdjusted = rowNumber;		
		MutableParameters mutableParam = mutableParamRepository.findRow(activeFileId, rowAdjusted);
		
		MutableParametersDTO mutableParamDTO = modelMapper.map(mutableParam, MutableParametersDTO.class);

        return mutableParamDTO;
	}
	
	public void setMutableParametersForRow(int activeFileId, int rowNumber, MutableParametersDTO mutableParamsDTO) {
		
		MutableParameters mutableParam = mutableParamRepository.findRow(activeFileId, rowNumber);
		
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
}
