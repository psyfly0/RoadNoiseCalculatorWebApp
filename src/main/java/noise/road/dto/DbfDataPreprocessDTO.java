package noise.road.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbfDataPreprocessDTO {
	
	private Integer identifier;
	private Integer kmh1;
	private Integer acCatDay1;
	private Integer acCatDay2;
	private Integer acCatDay3;
	private Integer acCatNight1;
	private Integer acCatNight2;
	private Integer acCatNight3;
	private Integer reverseIdentifier;
	private Integer reverseKmh1;
	private Integer reverseAcCatDay1;
	private Integer reverseAcCatDay2;
	private Integer reverseAcCatDay3;
	private Integer reverseAcCatNight1;
	private Integer reverseAcCatNight2;
	private Integer reverseAcCatNight3;

}
