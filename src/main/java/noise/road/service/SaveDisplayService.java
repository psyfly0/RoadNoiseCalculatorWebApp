package noise.road.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Geometry;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import noise.road.dto.DbfDataDTO;
import noise.road.dto.DbfDataPreprocessDTO;

import noise.road.entity.DbfData;
import noise.road.entity.Results;
import noise.road.entity.ShapeGeometry;
import noise.road.repository.DbfDataRepository;
import noise.road.repository.ResultsRepository;
import noise.road.repository.ShapeGeometryRepository;

@Service
public class SaveDisplayService {
	
	private int file_id = 1;
	
	@Autowired
	private DbfDataRepository dbfDataRepository;
	
	@Autowired
	private ResultsRepository resultsRepository;
	
	@Autowired
	private ShapeGeometryRepository shapeGeometryRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	public void saveData(List<DbfDataPreprocessDTO> dbfDataDTO, String fileName, List<Geometry> geometries) throws IOException, IllegalArgumentException, DataAccessException {
		
		List<DbfData> dbfDataList = new ArrayList<>();
		List<Results> resultsList = new ArrayList<>();
		List<ShapeGeometry> shapeGeometries = new ArrayList<>();
		
		String fName = removeExtensionFromName(fileName);
		int file_unique_id = 1;
		
		// ATTRIBUTES
		
		for (DbfDataPreprocessDTO dto : dbfDataDTO) {
			DbfData dbfData = new DbfData();
			
			dbfData.setFile_id(file_id);
			dbfData.setFileName(fName);
			dbfData.setFile_unique_id(file_unique_id);
			
			dbfData.setIdentifier(dto.getIdentifier());
			dbfData.setSpeed1(speed1and2(dto.getKmh1()));
			dbfData.setSpeed2(speed1and2(dto.getKmh1()));
			dbfData.setSpeed3(speed3(dto.getKmh1()));
			dbfData.setAcousticCatDay1(dto.getAcCatDay1());
			dbfData.setAcousticCatDay2(dto.getAcCatDay2());
			dbfData.setAcousticCatDay3(dto.getAcCatDay3());
			dbfData.setAcousticCatNight1(dto.getAcCatNight1());
			dbfData.setAcousticCatNight2(dto.getAcCatNight2());
			dbfData.setAcousticCatNight3(dto.getAcCatNight3());
			
			// reverse direction
			dbfData.setIdentifierR(dto.getReverseIdentifier());
			dbfData.setSpeed1R(speed1and2(dto.getReverseKmh1()));
			dbfData.setSpeed2R(speed1and2(dto.getReverseKmh1()));
			dbfData.setSpeed3R(speed3(dto.getReverseKmh1()));
			dbfData.setAcousticCatDay1R(dto.getReverseAcCatDay1());
			dbfData.setAcousticCatDay2R(dto.getReverseAcCatDay2());
			dbfData.setAcousticCatDay3R(dto.getReverseAcCatDay3());
			dbfData.setAcousticCatNight1R(dto.getReverseAcCatNight1());
			dbfData.setAcousticCatNight2R(dto.getReverseAcCatNight2());
			dbfData.setAcousticCatNight3R(dto.getReverseAcCatNight3());
			
			dbfDataList.add(dbfData);
			
			// Create a new Results instance for each DbfData entry
			Results results = new Results();
			results.setDbfData(dbfData);
			results.setFile_id(dbfData.getFile_id());
		    results.setFile_unique_id(dbfData.getFile_unique_id());
			
			resultsList.add(results);
			
			file_unique_id++;
		}
		
		// GEOMETRIES
		for (int i = 0; i < geometries.size(); i++) {
	        Geometry geometry = geometries.get(i);
	        
	        ShapeGeometry shapeGeometry = new ShapeGeometry();
	        
	        shapeGeometry.setDbfData(dbfDataList.get(i)); 
	        shapeGeometry.setFile_id(file_id);
	        shapeGeometry.setFile_unique_id(i + 1);
	        
	        String geometryWKT = geometry.toText();
	        shapeGeometry.setGeometryWKT(geometryWKT);

	        shapeGeometries.add(shapeGeometry);
		}
		
		dbfDataRepository.saveAll(dbfDataList);
		resultsRepository.saveAll(resultsList);
		shapeGeometryRepository.saveAll(shapeGeometries);
		
		file_id++;
	}
	
    public Map<Integer, List<DbfDataDTO>> getAll() {
        List<DbfData> dbfDataList = dbfDataRepository.findAll();
        List<Results> resultsList = resultsRepository.findAll();

        Map<Integer, List<Results>> resultsMapByFileId = resultsList.stream()
                .collect(Collectors.groupingBy(Results::getFile_id));

        Map<Integer, List<DbfData>> dbfDataMapByFileId = dbfDataList.stream()
                .collect(Collectors.groupingBy(DbfData::getFile_id));

        Map<Integer, List<DbfDataDTO>> dtoMapByFileId = new HashMap<>();

        for (Map.Entry<Integer, List<DbfData>> dbfDataEntry : dbfDataMapByFileId.entrySet()) {
            List<DbfDataDTO> dtos = new ArrayList<>();

            List<DbfData> dbfData = dbfDataEntry.getValue();
            List<Results> results = resultsMapByFileId.getOrDefault(dbfDataEntry.getKey(), Collections.emptyList());

            for (DbfData data : dbfData) {
                DbfDataDTO dto = modelMapper.map(data, DbfDataDTO.class);

                // Find corresponding results if available and map
                Results matchingResult = results.stream()
                        .filter(result -> result.getFile_unique_id().equals(data.getFile_unique_id()))
                        .findFirst()
                        .orElse(null);

                if (matchingResult != null) {
                    modelMapper.map(matchingResult, dto);
                }

                dtos.add(dto);
            }

            dtoMapByFileId.put(dbfDataEntry.getKey(), dtos);
        }

        return dtoMapByFileId;
    }
    
    private Integer speed1and2(Integer speed) {
    	if (speed > 0 && speed < 30) {
    		return 30;
    	}
    	return speed;
    }

	private Integer speed3(Integer speed) {
		if (speed > 0 && speed < 30) {
			return 30;
		} else if (speed <= 70) {
			return speed;
		} else if (speed > 70 && speed <= 90) {
			return 70;
		} else {
			return 80;
		}
	}
	
	private String removeExtensionFromName(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}
	
	
}
