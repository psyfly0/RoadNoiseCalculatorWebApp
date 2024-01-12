package noise.road.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import noise.road.dto.DbfDataPreprocessDTO;
import noise.road.entity.DbfData;
import noise.road.entity.MutableParameters;
import noise.road.entity.Results;
import noise.road.entity.ShapeGeometry;
import noise.road.repository.DbfDataRepository;
import noise.road.repository.MutableParametersRepository;
import noise.road.repository.ResultsRepository;
import noise.road.repository.ShapeGeometryRepository;
import noise.road.service.guestTables.GuestTables;

@Service
public class DisplayDataForGuestsService {

	@Autowired
	private DbfDataRepository dbfDataRepository;
	
	@Autowired
	private ResultsRepository resultsRepository;
	
	@Autowired
	private ShapeGeometryRepository shpGeometryRepository;
	
	@Autowired
	private MutableParametersRepository mutableParamsRepository;
	
	public void populateTablesForGuest() throws IOException, IllegalArgumentException, DataAccessException {
		
		if (dbfDataRepository.findAll().size() == 0) {
		
			List<DbfData> dbfDataList = GuestTables.createAttributes();
			List<MutableParameters> paramList = GuestTables.createMutableParams();
			List<Results> resultsList = GuestTables.createResults(dbfDataList);
			List<ShapeGeometry> shapeGeometries = GuestTables.createGeometries(dbfDataList);
		
			dbfDataRepository.saveAll(dbfDataList);
			mutableParamsRepository.saveAll(paramList);	
			resultsRepository.saveAll(resultsList);
			shpGeometryRepository.saveAll(shapeGeometries);
		}
	}
	
	
	
}
