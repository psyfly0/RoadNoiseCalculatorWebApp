package noise.road.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="DBF_DATA")
public class DbfData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "FILE_ID")
	private Integer file_id;
	
	@Column(name = "FILE_NAME")
	private String fileName;
	
	@Column(name = "FILE_UNIQUE_ID")
	private Integer file_unique_id;
	
	@Column(name = "IDENTIFIER")
	private Integer identifier;
	
	@Column(name = "SPEED_1")
	private Integer speed1;
	
	@Column(name = "SPEED_2")
	private Integer speed2;
	
	@Column(name = "SPEED_3")
	private Integer speed3;
	
	@Column(name = "ACOUSTIC_CAT_DAY_1")
	private Integer acousticCatDay1;
	
	@Column(name = "ACOUSTIC_CAT_DAY_2")
	private Integer acousticCatDay2;
	
	@Column(name = "ACOUSTIC_CAT_DAY_3")
	private Integer acousticCatDay3;
	
	@Column(name = "ACOUSTIC_CAT_NIGHT_1")
	private Integer acousticCatNight1;
	
	@Column(name = "ACOUSTIC_CAT_NIGHT_2")
	private Integer acousticCatNight2;
	
	@Column(name = "ACOUSTIC_CAT_NIGHT_3")
	private Integer acousticCatNight3;
	
	@Column(name = "R_IDENTIFIER")
	private Integer identifierR;
	
	@Column(name = "R_SPEED_1")
	private Integer speed1R;
	
	@Column(name = "R_SPEED_2")
	private Integer speed2R;
	
	@Column(name = "R_SPEED_3")
	private Integer speed3R;
	
	@Column(name = "R_ACOUSTIC_CAT_DAY_1")
	private Integer acousticCatDayR1;
	
	@Column(name = "R_ACOUSTIC_CAT_DAY_2")
	private Integer acousticCatDayR2;
	
	@Column(name = "R_ACOUSTIC_CAT_DAY_3")
	private Integer acousticCatDayR3;
	
	@Column(name = "R_ACOUSTIC_CAT_NIGHT_1")
	private Integer acousticCatNightR1;
	
	@Column(name = "R_ACOUSTIC_CAT_NIGHT_2")
	private Integer acousticCatNightR2;
	
	@Column(name = "R_ACOUSTIC_CAT_NIGHT_3")
	private Integer acousticCatNightR3;
	
	@Column(name = "LAEQ_DAY")
	private Double laeqDay;
	
	@Column(name = "LAEQ_NIGHT")
	private Double laeqNight;
	
	@Column(name = "LW_DAY")
	private Double lwDay;
	
	@Column(name = "LW_NIGHT")
	private Double lwNight;
	
	@Column(name = "IMPACT_AREA_DAY")
	private Double impactAreaDay;
	
	@Column(name = "IMPACT_AREA_NIGHT")
	private Double impactAreaNight;
	
	@Column(name = "PROTECTIVE_DISTANCE_DAY")
	private Double protectiveDistanceDay;
	
	@Column(name = "PROTECTIVE_DISTANCE_NIGHT")
	private Double protectiveDistanceNight;
	
	
}
