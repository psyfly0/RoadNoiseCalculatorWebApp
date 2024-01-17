package noise.road.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import noise.road.authenticationModel.UserFileCounter;
import noise.road.dto.MutableParametersDTO;
import noise.road.entity.MutableParameters;
import noise.road.repository.MutableParametersRepository;
import noise.road.repository.UserFileCounterRepository;

@Service
public class MutableParametersService {
	private final static String STEADY ="egyenletes";
	private final static String ACCELERATING ="gyorsuló";
	private final static String DECELERATING = "lassuló";
	
	@Autowired
	private MutableParametersRepository mpRepository;
	
	@Autowired
    private UserFileCounterRepository userFileCounterRepository;
	
	@Transactional
	public void saveInitialMutableParameters(MutableParametersDTO mpDTO, int dataLength, String username) throws IOException, IllegalArgumentException, DataAccessException {

		int fileId = 0;
		
		Optional<UserFileCounter> userFileCounterOptional = userFileCounterRepository.findByUsername(username);
		if (userFileCounterOptional.isPresent()) {
			UserFileCounter userFileCounter = userFileCounterOptional.get();
	        fileId = userFileCounter.getFileCounter() - 1;
		}


		List<MutableParameters> mutableParamsToUpdate = mpRepository.findByFileId(fileId);      
		List<MutableParameters> paramList = new ArrayList<>();	
		
		for (int i = 0; i < dataLength; i++) {
			MutableParameters param = mutableParamsToUpdate.get(i);
			
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
			 
		}
	
		mpRepository.saveAll(paramList);
	}
	
	public double calculateCategoryIparameterP(double slopeElevation, String trafficType) {
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
	
	public double calculateCategoryIIandIIIparameterP(double slopeElevation, String trafficType) {
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

