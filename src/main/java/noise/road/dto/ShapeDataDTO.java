package noise.road.dto;

import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeatureType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShapeDataDTO {

	private List<Geometry> geometries;	
    private SimpleFeatureType schema;
    private List<Map<String, Object>> attributeData;

}
