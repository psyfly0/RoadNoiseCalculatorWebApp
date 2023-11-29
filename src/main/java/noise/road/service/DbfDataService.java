package noise.road.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import noise.road.dto.DbfDataPreprocessDTO;
import noise.road.entity.DbfData;
import noise.road.repository.DbfDataRepository;

@Service
public class DbfDataService {
	
	@Autowired
	private DbfDataRepository dbfDataRepository;
	
	public void saveDbfData(List<DbfDataPreprocessDTO> dbfDataDTO, String fileName) {
		List<DbfData> dbfDataList = new ArrayList<>();
		String fName = removeExtensionFromName(fileName);
		int file_id = 1;
		
		for (DbfDataPreprocessDTO dto : dbfDataDTO) {
			DbfData dbfData = new DbfData();
			
			dbfData.setFileName(fName);
			dbfData.setFile_id(file_id);
			
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
			
			file_id++;
		}
		
		dbfDataRepository.saveAll(dbfDataList);

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
