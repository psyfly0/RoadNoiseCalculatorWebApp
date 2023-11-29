package noise.road.dto;

import java.util.List;

import lombok.Data;

@Data
public class SaveDbfRequest {

	private String fileName;
    private List<DbfDataPreprocessDTO> mappedData;
}
