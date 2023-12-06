package noise.road.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MutableParametersDTO {

	private double lthDay;
	private double lthNight;
	private double roadSurfaceRoughness;
	private double reflection;
	private double soundAbsorptionFactor;
	private double angleOfView;
	private String trafficType;
	private double slopeElevation;

}
