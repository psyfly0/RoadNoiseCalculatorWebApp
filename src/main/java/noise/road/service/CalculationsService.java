package noise.road.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import noise.road.entity.DbfData;
import noise.road.repository.DbfDataRepository;

@Service
@Slf4j
public class CalculationsService {

	@Autowired
	private DbfDataRepository dbfDataRepository;
	
	public void calculateLAeq(int fileId) {
		List<DbfData> a = dbfDataRepository.findByFileId(fileId);
		log.info("fetched data: {}", a);
		
	}
}
