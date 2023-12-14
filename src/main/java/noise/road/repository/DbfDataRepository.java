package noise.road.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import noise.road.entity.DbfData;

public interface DbfDataRepository extends JpaRepository<DbfData, Integer> {
	
	@Query(value = "SELECT * FROM DBF_DATA WHERE FILE_ID = (SELECT MAX(FILE_ID) FROM DBF_DATA)", nativeQuery = true)
    List<DbfData> findAllByLatestFileId();
	
	@Query(value = "SELECT * FROM DBF_DATA WHERE FILE_ID = :fileId", nativeQuery = true)
	List<DbfData> findByFileId(@Param("fileId") int fileId);
	
	List<DbfData> findAll();
}
