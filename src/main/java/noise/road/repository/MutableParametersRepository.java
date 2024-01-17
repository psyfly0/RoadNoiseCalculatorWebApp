package noise.road.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import noise.road.entity.MutableParameters;

public interface MutableParametersRepository extends JpaRepository<MutableParameters, Integer> {
	
	@Query(value = "SELECT * FROM MUTABLE_PARAMETERS WHERE FILE_ID = :fileId", nativeQuery = true)
	List<MutableParameters> findByFileId(@Param("fileId") int fileId);
	
	@Query(value = "SELECT * FROM MUTABLE_PARAMETERS WHERE FILE_ID = :fileId AND FILE_UNIQUE_ID = :row", nativeQuery = true)
	MutableParameters findRow(@Param("fileId") int fileId, @Param("row") int row);
	
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM MUTABLE_PARAMETERS WHERE FILE_ID = :fileId AND FILE_UNIQUE_ID IN (:rowNumbers)", nativeQuery = true)
    void deleteRowsByFileIdAndRowNumbers(@Param("fileId") int fileId, @Param("rowNumbers") List<Integer> rowNumbers);
	
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM MUTABLE_PARAMETERS", nativeQuery = true)
    void deleteAllData();

	@Transactional
    @Modifying
    @Query(value = "DROP TABLE MUTABLE_PARAMETERS", nativeQuery = true)
    void dropTable();
}
