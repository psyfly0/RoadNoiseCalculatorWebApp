package noise.road.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
	
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM RESULTS WHERE FILE_ID = :fileId AND FILE_UNIQUE_ID IN (:rowNumbers)", nativeQuery = true)
    void deleteRowsByFileIdAndRowNumbers(@Param("fileId") int fileId, @Param("rowNumbers") List<Integer> rowNumbers);
	
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM RESULTS", nativeQuery = true)
    void deleteAllData();
	
	@Transactional
    @Modifying
    @Query(value = "DROP TABLE RESULTS", nativeQuery = true)
    void dropTable();
}
