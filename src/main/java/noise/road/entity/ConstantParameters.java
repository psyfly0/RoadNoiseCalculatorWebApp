package noise.road.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="CONSTANT_PARAMETERS")
public class ConstantParameters {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "A1_CATEGORY_I")
	private double a1_category_I;
	
	@Column(name = "A1_CATEGORY_II")
	private double a1_category_II;
	
	@Column(name = "A1_CATEGORY_III")
	private double a1_category_III;
	
	@Column(name = "B1_CATEGORY_I")
	private double b1_category_I;
	
	@Column(name = "B1_CATEGORY_II")
	private double b1_category_II;
	
	@Column(name = "B1_CATEGORY_III")
	private double b1_category_III;
	
	@Column(name = "C1_CATEGORY_I")
	private double c1_category_I;
	
	@Column(name = "C1_CATEGORY_II")
	private double c1_category_II;
	
	@Column(name = "C1_CATEGORY_III")
	private double c1_category_III;
	
	@Column(name = "D1_CATEGORY_I")
	private double d1_category_I;
	
	@Column(name = "D1_CATEGORY_II")
	private double d1_category_II;
	
	@Column(name = "D1_CATEGORY_III")
	private double d1_category_III;
	
	@Column(name = "E1_CATEGORY_I")
	private double e1_category_I;
	
	@Column(name = "E1_CATEGORY_II")
	private double e1_category_II;
	
	@Column(name = "E1_CATEGORY_III")
	private double e1_category_III;
	
	@Column(name = "F1_CATEGORY_I")
	private double f1_category_I;
	
	@Column(name = "F1_CATEGORY_II")
	private double f1_category_II;
	
	@Column(name = "F1_CATEGORY_III")
	private double f1_category_III;
	
	@Column(name = "KD_FACTOR")
	private double kdFactor;
	
	@Column(name = "REFERENCE_DISTANCE")
	private double refDistance;
	
	@Column(name = "PRESSURE_TO_INTENSITY_CONVERSION")
	private double pressureToIntensity;
	
	@Column(name = "DAY_HOURS")
	private double dayHours;
	
	@Column(name = "NIGHT_HOURS")
	private double nightHours;

}
