package noise.road.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import noise.road.dto.DbfDataPreprocessDTO;
import noise.road.dto.DisplayDataDTO;
import noise.road.entity.DbfData;
import noise.road.repository.DbfDataRepository;

@Service
public class DbfDataService {
	
	private int file_id = 1;
	
	@Autowired
	private DbfDataRepository dbfDataRepository;
	
	public void saveDbfData(List<DbfDataPreprocessDTO> dbfDataDTO, String fileName) {
		List<DbfData> dbfDataList = new ArrayList<>();
		String fName = removeExtensionFromName(fileName);
		int file_unique_id = 1;
		
		for (DbfDataPreprocessDTO dto : dbfDataDTO) {
			DbfData dbfData = new DbfData();
			
			dbfData.setFile_id(file_id);
			dbfData.setFileName(fName);
			dbfData.setFile_unique_id(file_unique_id);
			
			dbfData.setIdentifier(dto.getIdentifier());
			dbfData.setSpeed1(dto.getKmh1());
			dbfData.setSpeed2(speed2and3(dto.getKmh1()));
			dbfData.setSpeed3(speed2and3(dto.getKmh1()));
			dbfData.setAcousticCatDay1(dto.getAcCatDay1());
			dbfData.setAcousticCatDay2(dto.getAcCatDay2());
			dbfData.setAcousticCatDay3(dto.getAcCatDay3());
			dbfData.setAcousticCatNight1(dto.getAcCatNight1());
			dbfData.setAcousticCatNight2(dto.getAcCatNight2());
			dbfData.setAcousticCatNight3(dto.getAcCatNight3());
			
			// reverse direction
			dbfData.setIdentifierR(dto.getReverseIdentifier());
			dbfData.setSpeed1R(dto.getReverseKmh1());
			dbfData.setSpeed2R(speed2and3(dto.getReverseKmh1()));
			dbfData.setSpeed3R(speed2and3(dto.getReverseKmh1()));
			dbfData.setAcousticCatDayR1(dto.getReverseAcCatDay1());
			dbfData.setAcousticCatDayR2(dto.getReverseAcCatDay2());
			dbfData.setAcousticCatDayR3(dto.getReverseAcCatDay3());
			dbfData.setAcousticCatNightR1(dto.getReverseAcCatNight1());
			dbfData.setAcousticCatNightR2(dto.getReverseAcCatNight2());
			dbfData.setAcousticCatNightR3(dto.getReverseAcCatNight3());
			
			dbfDataList.add(dbfData);
			
			file_unique_id++;
		}
		
		dbfDataRepository.saveAll(dbfDataList);
		
		file_id++;
	}
	

	
	
	public Map<Integer, List<DisplayDataDTO>> getAll() {
		List<DbfData> allFiles = dbfDataRepository.findAll();
		List<DisplayDataDTO> displayDataList = mapToDisplayDTO(allFiles);
		return displayDataList.stream()
	            .collect(Collectors.groupingBy(DisplayDataDTO::getFile_id));
	}
	
	public List<DisplayDataDTO> getLatestSavedFile() {
        List<DbfData> latestFiles = dbfDataRepository.findAllByLatestFileId();
        List<DisplayDataDTO> displayDataList = mapToDisplayDTO(latestFiles);
        return displayDataList;
    }
	
    private List<DisplayDataDTO> mapToDisplayDTO(List<DbfData> dbfDataList) {
        return dbfDataList.stream()
                .map(this::mapToDisplayDTO)
                .collect(Collectors.toList());
    }

    private DisplayDataDTO mapToDisplayDTO(DbfData dbfData) {
        DisplayDataDTO displayDataDTO = new DisplayDataDTO();
        displayDataDTO.setFileName(dbfData.getFileName());
        displayDataDTO.setFile_id(dbfData.getFile_id());
        displayDataDTO.setIdentifier(dbfData.getIdentifier());
        displayDataDTO.setKmh1(dbfData.getSpeed1());
        displayDataDTO.setKmh2(dbfData.getSpeed2());
        displayDataDTO.setKmh3(dbfData.getSpeed3());
        displayDataDTO.setAcCatDay1(dbfData.getAcousticCatDay1());
        displayDataDTO.setAcCatDay2(dbfData.getAcousticCatDay2());
        displayDataDTO.setAcCatDay3(dbfData.getAcousticCatDay3());
        displayDataDTO.setAcCatNight1(dbfData.getAcousticCatNight1());
        displayDataDTO.setAcCatNight2(dbfData.getAcousticCatNight2());
        displayDataDTO.setAcCatNight3(dbfData.getAcousticCatNight3());
        
        // reverse direction
        displayDataDTO.setReverseIdentifier(dbfData.getIdentifierR());
        displayDataDTO.setReverseKmh1(dbfData.getSpeed1R());
        displayDataDTO.setReverseKmh2(dbfData.getSpeed2R());
        displayDataDTO.setReverseKmh3(dbfData.getSpeed3R());
        displayDataDTO.setReverseAcCatDay1(dbfData.getAcousticCatDayR1());
        displayDataDTO.setReverseAcCatDay2(dbfData.getAcousticCatDayR2());
        displayDataDTO.setReverseAcCatDay3(dbfData.getAcousticCatDayR3());
        displayDataDTO.setReverseAcCatNight1(dbfData.getAcousticCatNightR1());
        displayDataDTO.setReverseAcCatNight2(dbfData.getAcousticCatNightR2());
        displayDataDTO.setReverseAcCatNight3(dbfData.getAcousticCatNightR3());
        
        return displayDataDTO;
    }
    
	private Integer speed2and3(Integer speed1) {
		if (speed1 <= 70) {
			return speed1;
		} else if (speed1 > 70 && speed1 <= 90) {
			return 70;
		} else {
			return 80;
		}
	}
	
	private String removeExtensionFromName(String fileName) {
		return fileName.substring(0, fileName.lastIndexOf("."));
	}
	
	
}
