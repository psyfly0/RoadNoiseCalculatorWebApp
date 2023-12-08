package noise.road.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisplayDataDTO {
	
	private String fileName;
	private Integer file_id;
	private Integer identifier;
	private Integer kmh1;
	private Integer kmh2;
	private Integer kmh3;
	private Integer acCatDay1;
	private Integer acCatDay2;
	private Integer acCatDay3;
	private Integer acCatNight1;
	private Integer acCatNight2;
	private Integer acCatNight3;
	private Integer reverseIdentifier;
	private Integer reverseKmh1;
	private Integer reverseKmh2;
	private Integer reverseKmh3;
	private Integer reverseAcCatDay1;
	private Integer reverseAcCatDay2;
	private Integer reverseAcCatDay3;
	private Integer reverseAcCatNight1;
	private Integer reverseAcCatNight2;
	private Integer reverseAcCatNight3;
	private Double laeqDay;
	private Double laeqNight;
	private Double lwDay;
	private Double lwNight;
	private Double impactAreaDay;
	private Double impactAreaNight;
	private Double protectiveDistanceDay;
	private Double protectiveDistanceNight;
	
}

