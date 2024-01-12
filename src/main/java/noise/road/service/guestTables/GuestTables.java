package noise.road.service.guestTables;

import java.util.ArrayList;
import java.util.List;

import noise.road.entity.DbfData;
import noise.road.entity.MutableParameters;
import noise.road.entity.Results;
import noise.road.entity.ShapeGeometry;

public class GuestTables {
	
	static final int DATA_SIZE = 4;
	static final String GEOMETRY_1 = "MULTILINESTRING ((545043.744996 268045.571687, 545083.715195 268070.432057, 545113.348447 268096.033815, 545142.932338 268123.082232, 545163.954314 268145.279949))";
	static final String GEOMETRY_2 = "MULTILINESTRING ((545163.954314 268145.279949, 545195.73921 268169.620379, 545224.211872 268184.901653, 545266.190163 268204.723227, 545283.439191 268213.088564, 545297.675319 268220.729398))";
	
	public static List<DbfData> createAttributes() {
		
		List<DbfData> dbfDataList = new ArrayList<>();
		
		DbfData dbfData = new DbfData();
		
		// first file
		// first row
		dbfData.setFile_id(1);
		dbfData.setFileName("First_file");
		dbfData.setFile_unique_id(1);
		
		dbfData.setIdentifier(1234);
		dbfData.setSpeed1(90);
		dbfData.setSpeed2(70);
		dbfData.setSpeed3(70);
		dbfData.setAcousticCatDay1(1500);
		dbfData.setAcousticCatDay2(300);
		dbfData.setAcousticCatDay3(450);
		dbfData.setAcousticCatNight1(300);
		dbfData.setAcousticCatNight2(50);
		dbfData.setAcousticCatNight3(25);
		
		// reverse direction
		dbfData.setIdentifierR(1234);
		dbfData.setSpeed1R(90);
		dbfData.setSpeed2R(70);
		dbfData.setSpeed3R(70);
		dbfData.setAcousticCatDay1R(1550);
		dbfData.setAcousticCatDay2R(350);
		dbfData.setAcousticCatDay3R(500);
		dbfData.setAcousticCatNight1R(350);
		dbfData.setAcousticCatNight2R(75);
		dbfData.setAcousticCatNight3R(50);
		
		dbfDataList.add(dbfData);
		
		dbfData = new DbfData();
		// second row
		dbfData.setFile_id(1);
		dbfData.setFileName("First_file");
		dbfData.setFile_unique_id(2);
		
		dbfData.setIdentifier(9876);
		dbfData.setSpeed1(130);
		dbfData.setSpeed2(80);
		dbfData.setSpeed3(80);
		dbfData.setAcousticCatDay1(15000);
		dbfData.setAcousticCatDay2(3000);
		dbfData.setAcousticCatDay3(4500);
		dbfData.setAcousticCatNight1(3000);
		dbfData.setAcousticCatNight2(500);
		dbfData.setAcousticCatNight3(250);
		
		// reverse direction
		dbfData.setIdentifierR(9876);
		dbfData.setSpeed1R(0);
		dbfData.setSpeed2R(0);
		dbfData.setSpeed3R(0);
		dbfData.setAcousticCatDay1R(0);
		dbfData.setAcousticCatDay2R(0);
		dbfData.setAcousticCatDay3R(0);
		dbfData.setAcousticCatNight1R(0);
		dbfData.setAcousticCatNight2R(0);
		dbfData.setAcousticCatNight3R(0);
		
		dbfDataList.add(dbfData);
		
		dbfData = new DbfData();
		// second file
		// first row
		dbfData.setFile_id(2);
		dbfData.setFileName("Second_file");
		dbfData.setFile_unique_id(1);
		
		dbfData.setIdentifier(1234);
		dbfData.setSpeed1(90);
		dbfData.setSpeed2(70);
		dbfData.setSpeed3(70);
		dbfData.setAcousticCatDay1(150);
		dbfData.setAcousticCatDay2(30);
		dbfData.setAcousticCatDay3(45);
		dbfData.setAcousticCatNight1(30);
		dbfData.setAcousticCatNight2(5);
		dbfData.setAcousticCatNight3(2);
		
		// reverse direction
		dbfData.setIdentifierR(1234);
		dbfData.setSpeed1R(9);
		dbfData.setSpeed2R(7);
		dbfData.setSpeed3R(7);
		dbfData.setAcousticCatDay1R(155);
		dbfData.setAcousticCatDay2R(35);
		dbfData.setAcousticCatDay3R(50);
		dbfData.setAcousticCatNight1R(35);
		dbfData.setAcousticCatNight2R(7);
		dbfData.setAcousticCatNight3R(5);
		
		dbfDataList.add(dbfData);
		
		dbfData = new DbfData();
		// second row
		dbfData.setFile_id(2);
		dbfData.setFileName("Second_file");
		dbfData.setFile_unique_id(2);
		
		dbfData.setIdentifier(9876);
		dbfData.setSpeed1(130);
		dbfData.setSpeed2(80);
		dbfData.setSpeed3(80);
		dbfData.setAcousticCatDay1(7500);
		dbfData.setAcousticCatDay2(1500);
		dbfData.setAcousticCatDay3(2225);
		dbfData.setAcousticCatNight1(1500);
		dbfData.setAcousticCatNight2(250);
		dbfData.setAcousticCatNight3(125);
		
		// reverse direction
		dbfData.setIdentifierR(9876);
		dbfData.setSpeed1R(0);
		dbfData.setSpeed2R(0);
		dbfData.setSpeed3R(0);
		dbfData.setAcousticCatDay1R(0);
		dbfData.setAcousticCatDay2R(0);
		dbfData.setAcousticCatDay3R(0);
		dbfData.setAcousticCatNight1R(0);
		dbfData.setAcousticCatNight2R(0);
		dbfData.setAcousticCatNight3R(0);
		
		dbfDataList.add(dbfData);
		
		return dbfDataList;
	}
	
	public static List<MutableParameters> createMutableParams() {
		List<MutableParameters> paramList = new ArrayList<>();	
		
		MutableParameters param = new MutableParameters();
		
		// first file
		// first row		
		param.setFile_id(1);
		param.setFile_unique_id(1);
		
		param.setLthDay(65);
		param.setLthNight(55);
		param.setRoadSurfaceRoughness(0.29);
		param.setReflection(0.5);
		param.setSoundAbsorptionFactor(12.5);
		param.setAngleOfView(180.0);
		param.setTrafficType("steady");
		param.setSlopeElevation(0.0);
		param.setParameterP_cat1(0.0);
		param.setParameterP_cat2_3(0.0);
		
		paramList.add(param);
		
		param = new MutableParameters();
		// second row		
		param.setFile_id(1);
		param.setFile_unique_id(2);
		
		param.setLthDay(65);
		param.setLthNight(55);
		param.setRoadSurfaceRoughness(0.29);
		param.setReflection(0.5);
		param.setSoundAbsorptionFactor(12.5);
		param.setAngleOfView(180.0);
		param.setTrafficType("steady");
		param.setSlopeElevation(0.0);
		param.setParameterP_cat1(0.0);
		param.setParameterP_cat2_3(0.0);
		
		paramList.add(param);
		
		param = new MutableParameters();
		// second file
		// first row		
		param.setFile_id(2);
		param.setFile_unique_id(1);
		
		param.setLthDay(65);
		param.setLthNight(55);
		param.setRoadSurfaceRoughness(0.29);
		param.setReflection(0.5);
		param.setSoundAbsorptionFactor(12.5);
		param.setAngleOfView(180.0);
		param.setTrafficType("steady");
		param.setSlopeElevation(0.0);
		param.setParameterP_cat1(0.0);
		param.setParameterP_cat2_3(0.0);
		
		paramList.add(param);
		
		param = new MutableParameters();
		// second row		
		param.setFile_id(2);
		param.setFile_unique_id(2);
		
		param.setLthDay(65);
		param.setLthNight(55);
		param.setRoadSurfaceRoughness(0.29);
		param.setReflection(0.5);
		param.setSoundAbsorptionFactor(12.5);
		param.setAngleOfView(180.0);
		param.setTrafficType("steady");
		param.setSlopeElevation(0.0);
		param.setParameterP_cat1(0.0);
		param.setParameterP_cat2_3(0.0);
		
		paramList.add(param);
			
		return paramList;

	}
	
	public static List<Results> createResults(List<DbfData> dbfDataList) {
		
		List<Results> resultsList = new ArrayList<>();
		
		for (DbfData dbfData : dbfDataList) {
            Results results = new Results();
            results.setDbfData(dbfData);
            results.setFile_id(dbfData.getFile_id());
            results.setFile_unique_id(dbfData.getFile_unique_id());

            resultsList.add(results);
        }
		
		return resultsList;
	}
	
	public static List<ShapeGeometry> createGeometries(List<DbfData> dbfDataList) {
		
		List<ShapeGeometry> shapeGeometries = new ArrayList<>();
		
		for (int i = 0; i < DATA_SIZE; i++) {
			ShapeGeometry shapeGeometry = new ShapeGeometry();
			shapeGeometry.setDbfData(dbfDataList.get(i));
			shapeGeometry.setFile_id(dbfDataList.get(i).getFile_id());
			shapeGeometry.setFile_unique_id(dbfDataList.get(i).getFile_unique_id());
			
			if (i % 2 == 0) {
				shapeGeometry.setGeometryWKT(GEOMETRY_1);
			} else {
				shapeGeometry.setGeometryWKT(GEOMETRY_2);
			}
			
			shapeGeometries.add(shapeGeometry);
			
		}

		return shapeGeometries;
	}
}
