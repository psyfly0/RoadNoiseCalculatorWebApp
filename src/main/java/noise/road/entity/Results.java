package noise.road.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="RESULTS")
public class Results {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@OneToOne
    @JoinColumn(name = "DBF_DATA_ID")
    private DbfData dbfData;
	
	@Column(name = "FILE_ID")
	private Integer file_id;
	
	@Column(name = "FILE_UNIQUE_ID")
	private Integer file_unique_id;
	
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
	
	@Column(name = "NOISE_AT_GIVEN_DISTANCE_DAY")
	private Double noiseAtGivenDistanceDay;
	
	@Column(name = "NOISE_AT_GIVEN_DISTANCE_NIGHT")
	private Double noiseAtGivenDistanceNight;
	
	@Column(name = "DIFFERENCE_DAY_0")
	private Double differenceDay0;
	
	@Column(name = "DIFFERENCE_NIGHT_0")
	private Double differenceNight0;
	
	@Column(name = "DIFFERENCE_DAY_1")
	private Double differenceDay1;
	
	@Column(name = "DIFFERENCE_NIGHT_1")
	private Double differenceNight1;
	
	@Column(name = "DIFFERENCE_DAY_2")
	private Double differenceDay2;
	
	@Column(name = "DIFFERENCE_NIGHT_2")
	private Double differenceNight2;
	
}
