package noise.road.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import noise.road.repository.DbfDataRepository;

@Service
public class DbfDataService {
	
	@Autowired
	private DbfDataRepository dbfDataRepository;
	
	public void saveDbfData(List<Map<String, Object>> dbfData) {
		// logic to perform some modificatoins before saving to DB
	}
}
