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
@Table(name="SHP_GEOMETRY")
public class ShapeGeometry {

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
	
    @Column(name = "GEOMETRY_WKT", columnDefinition = "TEXT")
    private String geometryWKT;
}
