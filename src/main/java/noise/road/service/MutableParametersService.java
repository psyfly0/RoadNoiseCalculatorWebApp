package noise.road.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import noise.road.dto.MutableParametersDTO;
import noise.road.entity.MutableParameters;
import noise.road.repository.MutableParametersRepository;

@Service
public class MutableParametersService {
	private final static String STEADY ="egyenletes";
	private final static String ACCELERATING ="gyorsuló";
	private final static String DECELERATING = "lassuló";
	
	private int file_id = 1;
	
	@Autowired
	private MutableParametersRepository mpRepository;
	
	//@Transactional
	public void saveInitialMutableParameters(MutableParametersDTO mpDTO, int dataLength) {
		
		List<MutableParameters> paramList = new ArrayList<>();	
		int file_unique_id = 1;
		
		for (int i = 0; i < dataLength; i++) {
			MutableParameters param = new MutableParameters();
		
			param.setFile_id(file_id);
			param.setFile_unique_id(file_unique_id);
			
			param.setLthDay(mpDTO.getLthDay());
			param.setLthNight(mpDTO.getLthNight());
			param.setRoadSurfaceRoughness(mpDTO.getRoadSurfaceRoughness());
			param.setReflection(mpDTO.getReflection());
			param.setSoundAbsorptionFactor(mpDTO.getSoundAbsorptionFactor());
			param.setAngleOfView(mpDTO.getAngleOfView());
			param.setTrafficType(mpDTO.getTrafficType());
			param.setSlopeElevation(mpDTO.getSlopeElevation());
			param.setParameterP_cat1(calculateCategoryIparameterP(mpDTO.getSlopeElevation(), mpDTO.getTrafficType()));
			param.setParameterP_cat2_3(calculateCategoryIIandIIIparameterP(mpDTO.getSlopeElevation(), mpDTO.getTrafficType()));
			
			paramList.add(param);
			
			file_unique_id++;
			 
		}
	
		mpRepository.saveAll(paramList);
		
		file_id++;
	}
	
	private double calculateCategoryIparameterP(double slopeElevation, String trafficType) {
		if (slopeElevation == 0 && trafficType.equals(STEADY)) {
            return 0;
        } else {
            switch (trafficType) {
                case STEADY:
                    return slopeElevation;
                case ACCELERATING:
                    return slopeElevation + 2;
                case DECELERATING:
                    return slopeElevation - 1;
                default:
                    return 0;
            }
        }
	}
	
	private double calculateCategoryIIandIIIparameterP(double slopeElevation, String trafficType) {
		if (slopeElevation == 0 && trafficType.equals(STEADY)) {
            return 0;
        } else if (slopeElevation > 0) {
            switch (trafficType) {
                case STEADY:
                    return slopeElevation;
                case ACCELERATING:
                    return slopeElevation + 4;
                case DECELERATING:
                    return slopeElevation - 3;
                default:
                    return 0;
            }
        } else {
            switch (trafficType) {
            case STEADY:
                    return -slopeElevation;
            case ACCELERATING:
                    return slopeElevation + 4;
            case DECELERATING:
                    return -slopeElevation - 3;
                default:
                    return 0;
            }
        }
	}
}

