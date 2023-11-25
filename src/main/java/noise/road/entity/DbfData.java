package noise.road.entity;

import java.util.Collection;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	private Integer id;

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
	
	private Integer speed1R;
	private Integer speed2R;
	private Integer speed3R;
	private Integer acousticCatDayR1;
	private Integer acousticCatDayR2;
	private Integer acousticCatDayR3;
	private Integer acousticCatNightR1;
	private Integer acousticCatNightR2;
	private Integer acousticCatNightR3;
	
}
