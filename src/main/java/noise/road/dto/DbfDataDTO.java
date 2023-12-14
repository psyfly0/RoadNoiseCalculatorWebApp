package noise.road.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DbfDataDTO {

	private String fileName;
	private Integer file_id;
	private Integer identifier;
	private Integer speed1;
	private Integer speed2;
	private Integer speed3;
	private Integer acousticCatDay1;
	private Integer acousticCatDay2;
	private Integer acousticCatDay3;
	private Integer acousticCatNight1;
	private Integer acousticCatNight2;
	private Integer acousticCatNight3;
	private Integer identifierR;
	private Integer speed1R;
	private Integer speed2R;
	private Integer speed3R;
	private Integer acousticCatDayR1;
	private Integer acousticCatDayR2;
	private Integer acousticCatDayR3;
	private Integer acousticCatNightR1;
	private Integer acousticCatNightR2;
	private Integer acousticCatNightR3;
	private Double laeqDay;
	private Double laeqNight;
	private Double lwDay;
	private Double lwNight;
	private Double impactAreaDay;
	private Double impactAreaNight;
	private Double protectiveDistanceDay;
	private Double protectiveDistanceNight;
	private Double noiseAtGivenDistanceDay;
	private Double noiseAtGivenDistanceNight;
	private Double differenceDay0;
	private Double differenceNight0;
	private Double differenceDay1;
	private Double differenceNight1;
	private Double differenceDay2;
	private Double differenceNight2;
}

