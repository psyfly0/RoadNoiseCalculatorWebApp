package noise.road.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import noise.road.entity.Results;

public interface ResultsRepository extends CrudRepository<Results, Integer> {

	@Query(value = "SELECT * FROM RESULTS WHERE FILE_ID = :fileId", nativeQuery = true)
	List<Results> findByFileId(@Param("fileId") int fileId);
	
	List<Results> findAll();
}
