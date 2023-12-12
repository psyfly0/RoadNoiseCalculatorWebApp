package noise.road.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculationResults {

	private double[] laeq;
    private double[] lw;
    private double[] protectiveDistance;
    private double[] impactArea;
    private double[] noiseAtGivenDistance;
}
