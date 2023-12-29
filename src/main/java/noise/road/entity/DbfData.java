package noise.road.entity;

import jakarta.persistence.CascadeType;
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
	
	@OneToOne(mappedBy = "dbfData", cascade = CascadeType.ALL, orphanRemoval = true)
    private Results results;
	
	@OneToOne(mappedBy = "dbfData", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShapeGeometry shapeGeometry;
	
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
	
	@Column(name = "IDENTIFIER_R")
	private Integer identifierR;
	
	@Column(name = "SPEED_1_R")
	private Integer speed1R;
	
	@Column(name = "SPEED_2_R")
	private Integer speed2R;
	
	@Column(name = "SPEED_3_R")
	private Integer speed3R;
	
	@Column(name = "ACOUSTIC_CAT_DAY_1_R")
	private Integer acousticCatDay1R;
	
	@Column(name = "ACOUSTIC_CAT_DAY_2_R")
	private Integer acousticCatDay2R;
	
	@Column(name = "ACOUSTIC_CAT_DAY_3_R")
	private Integer acousticCatDay3R;
	
	@Column(name = "ACOUSTIC_CAT_NIGHT_1_R")
	private Integer acousticCatNight1R;
	
	@Column(name = "ACOUSTIC_CAT_NIGHT_2_R")
	private Integer acousticCatNight2R;
	
	@Column(name = "ACOUSTIC_CAT_NIGHT_3_R")
	private Integer acousticCatNight3R;
	
}
