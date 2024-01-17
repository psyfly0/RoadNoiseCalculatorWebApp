package noise.road.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import noise.road.entity.ConstantParameters;
import noise.road.repository.ConstantParametersRepository;

@Service
public class ConstantParametersService {

	@Autowired
	private ConstantParametersRepository cpRepository;
	
	@Transactional
	public void insertParametersToDatabase() {

		if (cpRepository.findById(1).orElse(null) == null) {		
			ConstantParameters param = new ConstantParameters();
			
			param.setA1_category_I(2.00);
			param.setA1_category_II(2.40);
			param.setA1_category_III(2.70);
			param.setB1_category_I(2.92);
			param.setB1_category_II(2.92);
			param.setB1_category_III(2.92);
			param.setC1_category_I(3.03);
			param.setC1_category_II(3.17);
			param.setC1_category_III(3.90);
			param.setD1_category_I(2.00);
			param.setD1_category_II(2.10);
			param.setD1_category_III(1.86);
			param.setE1_category_I(2.62);
			param.setE1_category_II(3.15);
			param.setE1_category_III(5.07);
			param.setF1_category_I(3.92);
			param.setF1_category_II(3.79);
			param.setF1_category_III(2.53);
			param.setAngleOfViewDefault(180);
			param.setKdFactor(16.30);
			param.setRefDistance(7.50);
			param.setPressureToIntensity(12.70);
			param.setDayHours(16.00);
			param.setNightHours(8.00);
			
			cpRepository.save(param);
		}
	}
	
}
