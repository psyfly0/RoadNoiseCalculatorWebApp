package noise.road.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import noise.road.dto.DbfDataDTO;
import noise.road.entity.DbfData;
import noise.road.entity.Results;
import noise.road.repository.DbfDataRepository;
import noise.road.repository.ResultsRepository;

@Service
public class SortService {

	@Autowired
    private DbfDataRepository dbfDataRepository;
    
    @Autowired
    private ResultsRepository resultsRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    public List<DbfDataDTO> sortDbfDataAndResultsByLaeqNight(int activeFileId) throws IllegalArgumentException, DataAccessException {
        // Fetch sorted results for a specific fileId
        List<Results> sortedResults = resultsRepository.findAllByFileIdOrderByLaeqNightDesc(activeFileId);
        
        // Fetch DbfData for the same fileId
        List<DbfData> dbfData = dbfDataRepository.findByFileId(activeFileId);
        
        // Map Results and DbfData to DbfDataDTO using ModelMapper
        List<DbfDataDTO> mergedSortedData = new ArrayList<>();
        
        for (Results result : sortedResults) {
            DbfData dbfDataItem = dbfData.stream()
                .filter(data -> data.getId().equals(result.getDbfData().getId()))
                .findFirst()
                .orElse(null);
            
            if (dbfDataItem != null) {
                // Map Results and DbfData to DbfDataDTO using ModelMapper
                DbfDataDTO dto = modelMapper.map(result, DbfDataDTO.class);
                modelMapper.map(dbfDataItem, dto); // Merge DbfData into DTO
                mergedSortedData.add(dto);
            }
        }
        
        return mergedSortedData;
    }
}
