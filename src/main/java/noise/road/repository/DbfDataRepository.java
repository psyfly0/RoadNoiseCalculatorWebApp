package noise.road.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import noise.road.entity.DbfData;

public interface DbfDataRepository extends CrudRepository<DbfData, Integer> {
	
	@Query(value = "SELECT * FROM DBF_DATA WHERE FILE_ID = (SELECT MAX(FILE_ID) FROM DBF_DATA)", nativeQuery = true)
    List<DbfData> findAllByLatestFileId();
}
