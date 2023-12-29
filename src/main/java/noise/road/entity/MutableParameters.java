package noise.road.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="MUTABLE_PARAMETERS")
public class MutableParameters {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "FILE_ID")
	private Integer file_id;

	@Column(name = "FILE_UNIQUE_ID")
	private Integer file_unique_id;
	
	@Column(name = "LTH_DAY")
	private double lthDay; // 50.0, 55.0, 60.0, 65.0
	
	@Column(name = "LTH_NIGHT")
	private double lthNight; // 40.0, 45.0, 50.0, 55.0
	
	@Column(name = "K_ROAD_SURFACE_ROUGHNESS")
	private double roadSurfaceRoughness; // 0.0, 0.29, 0.49, 0.67, 0.78
	
	@Column(name = "KR_REFLECTION")
	private double reflection; // 0.5, 1.0, 1.5, 2.0, 2.5, 3.5
	
	@Column(name = "C_SOUND_ABSORPTION_FACTOR")
	private double soundAbsorptionFactor; // 12.5, 15.0
	
	@Column(name = "ANGLE_OF_VIEW")
	private double angleOfView; // any positive
	
	@Column(name = "TRAFFIC_TYPE")
	private String trafficType; // steady, accelerating, decelerating
	
	@Column(name = "SLOPE_ELEVATION")
	private double slopeElevation; // any positive or negative
	
	@Column(name = "P_PARAMETER_CAT_1")
	private double parameterP_cat1; // calculated based on TRAFFIC_TYPE and SLOPE_ELEVATION
	
	@Column(name = "P_PARAMETER_CAT_2_3")
	private double parameterP_cat2_3; // calculated based on TRAFFIC_TYPE and SLOPE_ELEVATION
	

}
