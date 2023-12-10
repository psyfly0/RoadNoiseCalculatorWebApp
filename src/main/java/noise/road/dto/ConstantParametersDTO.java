package noise.road.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConstantParametersDTO {

	private double a1_category_I;
	private double a1_category_II;
	private double a1_category_III;
	private double b1_category_I;
	private double b1_category_II;
	private double b1_category_III;
	private double c1_category_I;
	private double c1_category_II;
	private double c1_category_III;
	private double d1_category_I;
	private double d1_category_II;
	private double d1_category_III;
	private double e1_category_I;
	private double e1_category_II;
	private double e1_category_III;
	private double f1_category_I;
	private double f1_category_II;
	private double f1_category_III;
	private double angleOfViewDefault;
	private double kdFactor;
	private double refDistance;
	private double pressureToIntensity;
	private double dayHours;
	private double nightHours;
}
