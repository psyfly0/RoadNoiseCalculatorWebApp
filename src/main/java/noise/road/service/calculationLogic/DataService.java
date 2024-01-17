package noise.road.service.calculationLogic;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import noise.road.dto.ConstantParametersDTO;
import noise.road.dto.DbfDataDTO;
import noise.road.dto.MutableParametersDTO;
import noise.road.entity.ConstantParameters;
import noise.road.entity.DbfData;
import noise.road.entity.MutableParameters;
import noise.road.repository.ConstantParametersRepository;
import noise.road.repository.DbfDataRepository;
import noise.road.repository.MutableParametersRepository;

@Service
@Slf4j
public class DataService {

    @Autowired
    private DbfDataRepository dbfDataRepository;
    
    @Autowired
    private ConstantParametersRepository cpRepository;
    
    @Autowired
    private MutableParametersRepository mpRepository;
    
    @Autowired
    private ModelMapper modelMapper;
    
    public List<DbfDataDTO> fetchDbfData(int fileId) {
    	List<DbfData> dbfDataList = dbfDataRepository.findByFileId(fileId);
    	return mapDbfDataListToDto(dbfDataList);
    }
    
    public ConstantParametersDTO fetchConstantParameters() {
    	ConstantParameters cpParameters = cpRepository.findById(1).orElse(null);
    	return mapCpParametersToDto(cpParameters);
    }
    
    public List<MutableParametersDTO> fetchMutableParameters(int fileId) {
    	List<MutableParameters> mpParametersList = mpRepository.findByFileId(fileId);
    	return mapMpParametersToDto(mpParametersList);
    }
    
    private List<DbfDataDTO> mapDbfDataListToDto(List<DbfData> dbfDataList) {
    	List<DbfDataDTO> mappedDbfDataDTOList = Optional.ofNullable(dbfDataList)
                .map(dataList -> dataList.stream()
                        .map(dbfData -> modelMapper.map(dbfData, DbfDataDTO.class))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        // Log the mapped data
        log.info("Mapped DbfDataDTO List: {}", mappedDbfDataDTOList);

        return mappedDbfDataDTOList;
    }
    
    private ConstantParametersDTO mapCpParametersToDto(ConstantParameters cpParameters) {
    	return Optional.ofNullable(cpParameters)
                .map(parameters -> modelMapper.map(parameters, ConstantParametersDTO.class))
                .orElse(null);
    }
    
    private List<MutableParametersDTO> mapMpParametersToDto(List<MutableParameters> mpParameters) {
    	return Optional.ofNullable(mpParameters)
                .map(parameters -> parameters.stream()
                        .map(mutableParameters -> modelMapper.map(mutableParameters, MutableParametersDTO.class))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
