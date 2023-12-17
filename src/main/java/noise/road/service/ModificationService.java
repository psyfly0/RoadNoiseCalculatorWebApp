package noise.road.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import noise.road.entity.DbfData;
import noise.road.repository.DbfDataRepository;

@Service
public class ModificationService {

	@Autowired
	private DbfDataRepository dbfDataRepository;
	
	public void modifyCellValue(int activeFileId, int row, String columnName, Integer updatedCellValue) {

		int rowAdjusted = row + 1;
		DbfData dbfData = dbfDataRepository.findRow(activeFileId, rowAdjusted);
		
		 try {
	            // Capitalize the columnName to match the setter method convention
	            String capitalizedColumnName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);

	            // Get the corresponding setter method
	            Method setterMethod = DbfData.class.getMethod("set" + capitalizedColumnName, Integer.class);

	            // Invoke the setter method to set the updated cell value
	            setterMethod.invoke(dbfData, updatedCellValue);

	            // Save the updated entity to the database
	            dbfDataRepository.save(dbfData);

	        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
	            // Handle exceptions
	            e.printStackTrace();
	            // You might want to throw an exception or handle it as per your application's logic
	        }
		
		
	
		//List<DbfData> dbfData = dbfDataRepository.findByFileId(activeFileId);
		
		
	}
}
