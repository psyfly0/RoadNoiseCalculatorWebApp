package noise.road.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.operation.buffer.BufferParameters;

import lombok.extern.slf4j.Slf4j;
import noise.road.entity.Results;
import noise.road.entity.ShapeGeometry;

@Slf4j
public class FileSaveLogic {

	public static String stringWKT_EOV() {
		return "PROJCS[\"HD72 / EOV\",\r\n"
				+ "    GEOGCS[\"HD72\",\r\n"
				+ "        DATUM[\"Hungarian_Datum_1972\",\r\n"
				+ "            SPHEROID[\"GRS 1967\",6378160,298.247167427],\r\n"
				+ "            TOWGS84[52.17,-71.82,-14.9,0,0,0,0]],\r\n"
				+ "        PRIMEM[\"Greenwich\",0,\r\n"
				+ "            AUTHORITY[\"EPSG\",\"8901\"]],\r\n"
				+ "        UNIT[\"degree\",0.0174532925199433,\r\n"
				+ "            AUTHORITY[\"EPSG\",\"9122\"]],\r\n"
				+ "        AUTHORITY[\"EPSG\",\"4237\"]],\r\n"
				+ "    PROJECTION[\"Hotine_Oblique_Mercator_Azimuth_Center\"],\r\n"
				+ "    PARAMETER[\"latitude_of_center\",47.1443937222222],\r\n"
				+ "    PARAMETER[\"longitude_of_center\",19.0485717777778],\r\n"
				+ "    PARAMETER[\"azimuth\",90],\r\n"
				+ "    PARAMETER[\"rectified_grid_angle\",90],\r\n"
				+ "    PARAMETER[\"scale_factor\",0.99993],\r\n"
				+ "    PARAMETER[\"false_easting\",650000],\r\n"
				+ "    PARAMETER[\"false_northing\",200000],\r\n"
				+ "    UNIT[\"metre\",1,\r\n"
				+ "        AUTHORITY[\"EPSG\",\"9001\"]],\r\n"
				+ "    AXIS[\"Easting\",EAST],\r\n"
				+ "    AXIS[\"Northing\",NORTH],\r\n"
				+ "    AUTHORITY[\"EPSG\",\"23700\"]]";
	}
	
	public static Class<?> determineColumnType(String columnName) {
		// for calculated fields: double, for input data: integer
		if (columnName.startsWith("L") || columnName.startsWith("V") || 
			columnName.startsWith("H") || columnName.startsWith("d") || 
			columnName.contains("különbsége")) {
			
			return Double.class;
		}
		return Integer.class;
	}
	
	public static String truncateColumnNames(String columnName) {
		String truncatedName = "";

        // Define the patterns and corresponding truncated names
        Map<String, String> patterns = new HashMap<>();
        patterns.put("Azo.*", "NO");
        patterns.put("I_km/h.*", "1_ak_kmh");
        patterns.put("II_km/h.*", "2_ak_kmh");
        patterns.put("III_km/h.*", "3_ak_kmh");
        patterns.put("I_ak_kat_N.*", "1_ak_N");
        patterns.put("II_ak_kat_N.*", "2_ak_N");
        patterns.put("III_ak_kat_N.*", "3_ak_N");
        patterns.put("I_ak_kat_E.*", "1_ak_E");
        patterns.put("II_ak_kat_E.*", "2_ak_E");
        patterns.put("III_ak_kat_E.*", "3_ak_E");
        patterns.put("R_Azo.*", "R_NO");
        patterns.put("R_I_km/h.*", "R_1_ak_kmh");
        patterns.put("R_II_km/h.*", "R_2_ak_kmh");
        patterns.put("R_III_km/h.*", "R_3_ak_kmh");
        patterns.put("R_I_ak_kat_N.*", "R_1_ak_N");
        patterns.put("R_II_ak_kat_N.*", "R_2_ak_N");
        patterns.put("R_III_ak_kat_N.*", "R_3_ak_N");
        patterns.put("R_I_ak_kat_E.*", "R_1_ak_E");
        patterns.put("R_II_ak_kat_E.*", "R_2_ak_E");
        patterns.put("R_III_ak_kat_E.*", "R_3_ak_E");
        patterns.put("LAeq N.*", "LAeq_N");
        patterns.put("LAeq É.*", "LAeq_E");
        patterns.put("LW N.*", "Lw_N");
        patterns.put("LW É.*", "Lw_E");
        patterns.put("Védőtávolság N.*", "Vedotav_N");
        patterns.put("Védőtávolság É.*", "Vedotav_E");
        patterns.put("Hatásterület N.*", "Hataster_N");
        patterns.put("Hatásterület É.*", "Hataster_E");
        
     // Iterate through the patterns and check for matches
        for (Map.Entry<String, String> entry : patterns.entrySet()) {
            String pattern = entry.getKey();
            String truncatedValue = entry.getValue();
            if (columnName.matches(pattern)) {
                truncatedName = truncatedValue;
                break;
            }
        }
        
     // Handle additional specific cases
        if (columnName.contains("távolságon Nappal")) {
        	int startIndex = columnName.indexOf("Zajterhelés") + "Zajterhelés".length();
        	int endIndex = columnName.indexOf("távolság");
        	
        	// get the substring between the above two
        	String extractedString = columnName.substring(startIndex, endIndex).trim();
        	return "dB_" + extractedString + "_mN";
        } else if(columnName.contains("távolságon Éjjel")) {
        	int startIndex = columnName.indexOf("Zajterhelés") + "Zajterhelés".length();
        	int endIndex = columnName.indexOf("távolság");
        	
        	// get the substring between the above two
        	String extractedString = columnName.substring(startIndex, endIndex).trim();
        	return "dB_" + extractedString + "_mE";
        } else if(columnName.contains("különbsége Nappal")) {
        	return "kul_N";
        } else if(columnName.contains("különbsége Éjjel")) {
        	return "kul_E";
        }
        
        return truncatedName;

	}
	
	public static Geometry densifyGeometry(Geometry geometry, double step) {
        GeometryFactory factory = geometry.getFactory();
        Coordinate[] coordinates = geometry.getCoordinates();
        List<Coordinate> densifiedCoordinates = new ArrayList<>();

        for (int i = 0; i < coordinates.length - 1; i++) {
            Coordinate start = coordinates[i];
            Coordinate end = coordinates[i + 1];
            double length = start.distance(end);

            int numSteps = (int) Math.ceil(length / step);
            double dx = (end.x - start.x) / numSteps;
            double dy = (end.y - start.y) / numSteps;

            for (int j = 0; j < numSteps; j++) {
                double x = start.x + j * dx;
                double y = start.y + j * dy;
                densifiedCoordinates.add(new Coordinate(x, y));
            }
        }

        densifiedCoordinates.add(coordinates[coordinates.length - 1]); // Add the last coordinate

        return factory.createLineString(densifiedCoordinates.toArray(new Coordinate[0]));
	}
	
	public static void addNonNullAttribute(SimpleFeatureBuilder featureBuilder, Object value) {
		if (value != null) {
            featureBuilder.add(value);
        }
	}
	
	public static void addToZipFile(File file, String fileName, ZipOutputStream zipOut) throws IOException {
	    try (FileInputStream fis = new FileInputStream(file)) {
	        ZipEntry zipEntry = new ZipEntry(fileName);
	        zipOut.putNextEntry(zipEntry);

	        byte[] bytes = new byte[1024];
	        int length;
	        while ((length = fis.read(bytes)) >= 0) {
	            zipOut.write(bytes, 0, length);
	        }
	    }
	}
	
	public static List<Geometry> createNewGeometry(List<ShapeGeometry> shpGeometryList, List<Results> resultsList, String saveName) {
		long startTime = System.nanoTime();
		List<Geometry> newGeometries = new ArrayList<>();
		
		BufferParameters bufferParameters = new BufferParameters();
	    bufferParameters.setEndCapStyle(BufferParameters.CAP_ROUND);
	    bufferParameters.setJoinStyle(BufferParameters.JOIN_ROUND);
	    int quadrantSegments = 8;
	    // Create a WKT reader
    	WKTReader wktReader = new WKTReader();
    	
	    // day
	    Geometry bufferDay = null;
	    for (int i = 0; i < shpGeometryList.size(); i++) {
	    	ShapeGeometry shapeGeometry = shpGeometryList.get(i);
	    	String geometryWKT = shapeGeometry.getGeometryWKT();
		
	    	
	 
	    		// Parse the WKT string to a Geometry object
	    		Geometry geometry = null;
				try {
					geometry = wktReader.read(geometryWKT);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			Results results = resultsList.get(i);
	    	double distanceDay = saveName.equals("vedotav") ? results.getProtectiveDistanceDay() : results.getImpactAreaDay();
				
	    	Geometry bufferGeomDay = geometry.buffer(distanceDay, quadrantSegments, bufferParameters.getEndCapStyle());
	    	
	    	if (bufferDay == null) {
	    		bufferDay = bufferGeomDay;
	    	} else {
	    		bufferDay = bufferDay.union(bufferGeomDay);
	    	}
	    }
	    bufferDay = bufferDay.getBoundary(); // Convert to a LineString
        newGeometries.add(bufferDay);
        
        // night
        Geometry bufferNight = null;
        for (int i = 0; i < shpGeometryList.size(); i++) {
	    	ShapeGeometry shapeGeometry = shpGeometryList.get(i);
	    	String geometryWKT = shapeGeometry.getGeometryWKT();

    		// Parse the WKT string to a Geometry object
    		Geometry geometry = null;
			try {
				geometry = wktReader.read(geometryWKT);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
	    	Results results = resultsList.get(i);
	    	double distanceNight = saveName.equals("vedotav") ? results.getProtectiveDistanceNight(): results.getImpactAreaNight();
	    	
	    	Geometry bufferGeomNight = geometry.buffer(distanceNight, quadrantSegments, bufferParameters.getEndCapStyle());
	    	
	    	if (bufferNight == null) {
	    		bufferNight = bufferGeomNight;
	    	} else {
	    		bufferNight = bufferNight.union(bufferGeomNight);
	    	}
        }
        bufferNight = bufferNight.getBoundary(); // Convert to a LineString
        newGeometries.add(bufferNight);
        
        long endTime = System.nanoTime();
        long durationInNano = endTime - startTime;
        double durationInSeconds = durationInNano / 1_000_000_000.0;
        log.info("createNewGeometry duration in sec: {}", durationInSeconds);
        return newGeometries;
		
	}
	
	
}
