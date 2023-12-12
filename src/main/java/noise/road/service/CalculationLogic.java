package noise.road.service;

import lombok.extern.slf4j.Slf4j;
import noise.road.dto.ConstantParametersDTO;
import noise.road.dto.DbfDataDTO;
import noise.road.dto.MutableParametersDTO;

@Slf4j
public class CalculationLogic {

	public CalculationResults perfromCalculations(DbfDataDTO dbfDataDTO, 
							MutableParametersDTO mutableParamDTO, 
							ConstantParametersDTO constantParamDTO, Double distanceToCalculate) {
		
		log.info("dbfDataDTO: {}", dbfDataDTO);
		log.info("mutableParamsDTO: {}", mutableParamDTO);
		log.info("constantParamsDTO {}", constantParamDTO);
		log.info("distanceToCalculate {}", distanceToCalculate);
		
		boolean isDistanceGiven = false;
		// partial results
		double[] vehicleSpeed = vehicleSpeed(dbfDataDTO);
		double[] vehicleHours = vehicleHours(dbfDataDTO, constantParamDTO);
		
		double[] ktFactor = ktFactor(dbfDataDTO, mutableParamDTO, constantParamDTO, vehicleSpeed);	
		double[] kdFactor = kdFactor(constantParamDTO, vehicleSpeed, vehicleHours);
		
		// results
		double[] laeq = laeq(ktFactor, kdFactor);	
		double[] lw = lw(laeq, constantParamDTO);
		
		double[] protectiveDistance = protectiveDistance(laeq, mutableParamDTO, constantParamDTO);
		double[] impactArea = impactArea(laeq, mutableParamDTO, constantParamDTO);
		
		double[] noiseAtGivenDistance = null;
		if (distanceToCalculate != null) {
			noiseAtGivenDistance = noiseAtGivenDistance(distanceToCalculate, laeq, mutableParamDTO, constantParamDTO);
			isDistanceGiven = true;
		}
		
		CalculationResults results = new CalculationResults();
		results.setLaeq(laeq);
		results.setLw(lw);
		results.setProtectiveDistance(protectiveDistance);
		results.setImpactArea(impactArea);
		
		if (isDistanceGiven) {
			results.setNoiseAtGivenDistance(noiseAtGivenDistance);
		}
		
		return results;
		
	}
	
	private double[] noiseAtGivenDistance(Double distance, double[] laeq, 
									MutableParametersDTO mutableParamDTO, 
									ConstantParametersDTO constantParamDTO) {
		
		double[] noiseAtGivenDistance = new double[2];
		
		//day
		noiseAtGivenDistance[0] = Math.round((laeq[0] + (mutableParamDTO.getSoundAbsorptionFactor() 
				* Math.log10(constantParamDTO.getRefDistance() / distance)) +
                (10 * Math.log10(mutableParamDTO.getAngleOfView() /
        		constantParamDTO.getAngleOfViewDefault())) + mutableParamDTO.getReflection()) * 10) / 10.0;
		
		//night
		noiseAtGivenDistance[1] = Math.round((laeq[1] + (mutableParamDTO.getSoundAbsorptionFactor() 
				* Math.log10(constantParamDTO.getRefDistance() / distance)) +
                (10 * Math.log10(mutableParamDTO.getAngleOfView() /
        		constantParamDTO.getAngleOfViewDefault())) + mutableParamDTO.getReflection()) * 10) / 10.0;
		
		return noiseAtGivenDistance;
	}
	
	private double[] impactArea(double[] laeq, 
							MutableParametersDTO mutableParamDTO, 
							ConstantParametersDTO constantParamDTO) {
		
		double[] impactArea = new double[2];
		
		// day
		impactArea[0] = Math.round((constantParamDTO.getRefDistance() /
                Math.pow(10, (((mutableParamDTO.getLthDay() - 10) - laeq[0] - 
                (10 * Math.log10(mutableParamDTO.getAngleOfView() / constantParamDTO.getAngleOfViewDefault()))) 
        		- mutableParamDTO.getReflection()) / mutableParamDTO.getSoundAbsorptionFactor())) * 10) / 10.0;
		
		// night
		impactArea[1] = Math.round((constantParamDTO.getRefDistance() /
                Math.pow(10, (((mutableParamDTO.getLthNight() - 10) - laeq[1] - 
                (10 * Math.log10(mutableParamDTO.getAngleOfView() / constantParamDTO.getAngleOfViewDefault()))) 
        		- mutableParamDTO.getReflection()) / mutableParamDTO.getSoundAbsorptionFactor())) * 10) / 10.0;
		
		return impactArea;
		
	}
	
	private double[] protectiveDistance(double[] laeq, 
									MutableParametersDTO mutableParamDTO, 
									ConstantParametersDTO constantParamDTO) {
		
		double[] protectiveDistance = new double[2];
		
		// day
		protectiveDistance[0] = Math.round((constantParamDTO.getRefDistance() /
                Math.pow(10, ((mutableParamDTO.getLthDay() - laeq[0] - 
                (10 * Math.log10(mutableParamDTO.getAngleOfView() / constantParamDTO.getAngleOfViewDefault())))
        		- mutableParamDTO.getReflection()) / mutableParamDTO.getSoundAbsorptionFactor())) * 10) / 10.0;
		
		// night
		protectiveDistance[1] = Math.round((constantParamDTO.getRefDistance() /
                Math.pow(10, ((mutableParamDTO.getLthNight() - laeq[1] - 
                (10 * Math.log10(mutableParamDTO.getAngleOfView() / constantParamDTO.getAngleOfViewDefault())))
        		- mutableParamDTO.getReflection()) / mutableParamDTO.getSoundAbsorptionFactor())) * 10) / 10.0;
		
		return protectiveDistance;
	}
	
	private double[] lw(double[] laeq, ConstantParametersDTO constantParamDTO) {
		double[] lw = new double[2];
		
		// day
		lw[0] = Math.round((laeq[0] + constantParamDTO.getPressureToIntensity()) * 10) / 10.0;
		
		// night
		lw[1] = Math.round((laeq[1] + constantParamDTO.getPressureToIntensity()) * 10) / 10.0;
		
		return lw;
	}
	
	private double[] laeq(double[] ktFactor, double[] kdFactor) {
		double[] laeq = new double[2];
		
		// day
		double laeqDay = 0.0;
		if (!Double.isNaN(kdFactor[0])) {
			laeqDay += Math.pow(10, 0.1 * (ktFactor[0] + kdFactor[0]));
		}
		
		if (!Double.isNaN(kdFactor[1])) {
			laeqDay += Math.pow(10,  0.1 * (ktFactor[1] + kdFactor[1]));
		}
		
		if (!Double.isNaN(kdFactor[2])) {
			laeqDay += Math.pow(10,  0.1 * (ktFactor[2] + kdFactor[2]));
		}
		
		laeq[0] = laeqDay != 0 ? (Math.round((10 * Math.log10(laeqDay)) * 10) / 10.0) : 0.0;
		
		// night
		double laeqNight = 0.0;
		if (!Double.isNaN(kdFactor[3])) {
			laeqNight += Math.pow(10, 0.1 * (ktFactor[0] + kdFactor[3]));
		}
		
		if (!Double.isNaN(kdFactor[4])) {
			laeqNight += Math.pow(10,  0.1 * (ktFactor[1] + kdFactor[4]));
		}
		
		if (!Double.isNaN(kdFactor[5])) {
			laeqNight += Math.pow(10,  0.1 * (ktFactor[2] + kdFactor[5]));
		}
		
		laeq[1] = laeqNight != 0 ? (Math.round((10 * Math.log10(laeqNight)) * 10) / 10.0) : 0.0;
		
		return laeq;
	}
	
	private double[] ktFactor(DbfDataDTO dbfDataDTO, 
							MutableParametersDTO mutableParamDTO, 
							ConstantParametersDTO constantParamDTO, 
							double[] vehicleSpeed) {
		
		double[] Kt = new double[3];
		
		Kt[0] = (vehicleSpeed[0] != 0) ? (10 * Math.log10(Math.pow(10, 
                constantParamDTO.getA1_category_I() + mutableParamDTO.getRoadSurfaceRoughness()
                + constantParamDTO.getB1_category_I() * Math.log10(vehicleSpeed[0])) +
                Math.pow(10, constantParamDTO.getC1_category_I() + 
        		constantParamDTO.getD1_category_I() *
                Math.log10(vehicleSpeed[0])) + Math.pow(10, constantParamDTO.getE1_category_I() +
        		constantParamDTO.getF1_category_I() * 
                Math.log10(11 + mutableParamDTO.getParameterP_cat1())))) : 0;
		
		Kt[1] = (vehicleSpeed[1] != 0) ? (10 * Math.log10(Math.pow(10, 
                constantParamDTO.getA1_category_II() + mutableParamDTO.getRoadSurfaceRoughness()
                + constantParamDTO.getB1_category_II() * Math.log10(vehicleSpeed[1])) +
                Math.pow(10, constantParamDTO.getC1_category_II() + 
                constantParamDTO.getD1_category_II() *
                Math.log10(vehicleSpeed[1])) + Math.pow(10, constantParamDTO.getE1_category_II() +
                constantParamDTO.getF1_category_II() * 
                Math.log10(11 + mutableParamDTO.getParameterP_cat2_3())))) : 0;
		
		Kt[2] = (vehicleSpeed[2] != 0) ? (10 * Math.log10(Math.pow(10, 
                constantParamDTO.getA1_category_III() + mutableParamDTO.getRoadSurfaceRoughness()
                + constantParamDTO.getB1_category_III() * Math.log10(vehicleSpeed[2])) +
                Math.pow(10, constantParamDTO.getC1_category_III() + 
                constantParamDTO.getD1_category_III() *
                Math.log10(vehicleSpeed[2])) + Math.pow(10, constantParamDTO.getE1_category_III() +
                constantParamDTO.getF1_category_III() * 
                Math.log10(11 + mutableParamDTO.getParameterP_cat2_3())))) : 0;
		
		return Kt;
	}
	
	private double[] kdFactor(ConstantParametersDTO constantParamDTO, 
							double[] vehicleSpeed, 
							double[] vehicleHours) {
		
		double[] KD = new double[6];
		
		// day
		KD[0] = (vehicleHours[0] != 0 && vehicleSpeed[0] != 0) ? 
				(10 * Math.log10(vehicleHours[0] /
                vehicleSpeed[0]) - constantParamDTO.getKdFactor()) : Double.NaN;
        KD[1] = (vehicleHours[1] != 0 && vehicleSpeed[1] != 0) ? (10 * 
                Math.log10(vehicleHours[1] /
                vehicleSpeed[1]) - constantParamDTO.getKdFactor()) : Double.NaN;
        KD[2] = (vehicleHours[2] != 0 && vehicleSpeed[2] != 0) ? (10 * 
                Math.log10(vehicleHours[2] /
                vehicleSpeed[2]) - constantParamDTO.getKdFactor()) : Double.NaN;
        
        // night
        KD[3] = (vehicleHours[3] != 0 && vehicleSpeed[0] != 0) ? (10 * 
                Math.log10(vehicleHours[3] /
                vehicleSpeed[0]) - constantParamDTO.getKdFactor()) : Double.NaN;
        KD[4] = (vehicleHours[4] != 0 && vehicleSpeed[1] != 0) ? (10 * 
                Math.log10(vehicleHours[4] /
                vehicleSpeed[1]) - constantParamDTO.getKdFactor()) : Double.NaN;
        KD[5] = (vehicleHours[5] != 0 && vehicleSpeed[2] != 0) ? (10 * 
                Math.log10(vehicleHours[5] /
                vehicleSpeed[2]) - constantParamDTO.getKdFactor()) : Double.NaN;
        
        return KD;
	}
	
	private double[] vehicleSpeed(DbfDataDTO dbfDataDTO) {
		double[] vehicleSpeed = new double[3];
		
		vehicleSpeed[0] = Math.max(dbfDataDTO.getSpeed1(), dbfDataDTO.getSpeed1R());
		vehicleSpeed[1] = Math.max(dbfDataDTO.getSpeed2(), dbfDataDTO.getSpeed2R());
		vehicleSpeed[2] = Math.max(dbfDataDTO.getSpeed3(), dbfDataDTO.getSpeed3R());
		
		return vehicleSpeed;
	}
	
	private double[] vehicleHours(DbfDataDTO dbfDataDTO, ConstantParametersDTO constantParamDTO) {
		double[] vehicleHours = new double[6];
		
		// day sum
		vehicleHours[0] = Math.ceil((double) dbfDataDTO.getAcousticCatDay1() / constantParamDTO.getDayHours() +
                (double) dbfDataDTO.getAcousticCatDayR1() / constantParamDTO.getDayHours());
		vehicleHours[1] = Math.ceil((double) dbfDataDTO.getAcousticCatDay2() / constantParamDTO.getDayHours() +
                (double) dbfDataDTO.getAcousticCatDayR2() / constantParamDTO.getDayHours());
		vehicleHours[2] = Math.ceil((double) dbfDataDTO.getAcousticCatDay3() / constantParamDTO.getDayHours() +
                (double) dbfDataDTO.getAcousticCatDayR3() / constantParamDTO.getDayHours());
		
		// night sum
		vehicleHours[3] = Math.ceil((double) dbfDataDTO.getAcousticCatNight1() / constantParamDTO.getNightHours() +
                (double) dbfDataDTO.getAcousticCatNightR1() / constantParamDTO.getNightHours());
		vehicleHours[4] = Math.ceil((double) dbfDataDTO.getAcousticCatNight2() / constantParamDTO.getNightHours() +
                (double) dbfDataDTO.getAcousticCatNightR2() / constantParamDTO.getNightHours());
		vehicleHours[5] = Math.ceil((double) dbfDataDTO.getAcousticCatNight3() / constantParamDTO.getNightHours() +
                (double) dbfDataDTO.getAcousticCatNightR3() / constantParamDTO.getNightHours());
		
		return vehicleHours;
	}
	

}
