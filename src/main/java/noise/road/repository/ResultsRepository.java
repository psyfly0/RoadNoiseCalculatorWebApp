package noise.road.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import noise.road.entity.Results;

public interface ResultsRepository extends JpaRepository<Results, Integer> {

	@Query(value = "SELECT * FROM RESULTS WHERE FILE_ID = :fileId", nativeQuery = true)
	List<Results> findByFileId(@Param("fileId") int fileId);
	
	@Query(value = "SELECT r.laeqDay, r.laeqNight, d.identifier " +
			"FROM Results r " +
			"JOIN r.dbfData d " +
			"WHERE r.file_id = :fileId " +
			"AND r.dbfData.id = d.id")
	List<Object[]> findLaeqAndIdentifierById(@Param("fileId") int fileId);
	
	@Query("SELECT r FROM Results r JOIN r.dbfData d WHERE d.file_id = :fileId ORDER BY r.laeqNight DESC")
    List<Results> findAllByFileIdOrderByLaeqNightDesc(@Param("fileId") int fileId);
	
	List<Results> findAll();
}
