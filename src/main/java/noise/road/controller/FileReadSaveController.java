package noise.road.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import noise.road.dto.ShapeDataDTO;
import noise.road.service.FileReadService;

@RestController
@RequestMapping("/console")
@Slf4j
public class FileReadSaveController {
	
	@Autowired
	private FileReadService fileReadService;

    @PostMapping("/fileLoad")
    public List<Map<String, Object>> fileLoad(@RequestParam("zipFile") MultipartFile zipFile) throws IOException {
        ShapeDataDTO shapeData = fileReadService.readShapefileFromZip(zipFile);
        List<Map<String, Object>> attributeData = shapeData.getAttributeData();
        return attributeData;     
    }

}
