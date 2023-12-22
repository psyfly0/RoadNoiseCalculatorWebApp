package noise.road.dto;

import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeatureType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShapeDataDTO {

	private List<Geometry> geometries;	
    private SimpleFeatureType schema;
    private List<Map<String, Object>> attributeData;

}
