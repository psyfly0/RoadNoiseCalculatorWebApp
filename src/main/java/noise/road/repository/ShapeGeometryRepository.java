package noise.road.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import noise.road.entity.ShapeGeometry;

public interface ShapeGeometryRepository extends CrudRepository<ShapeGeometry, Integer> {

	@Query(value = "SELECT * FROM SHP_GEOMETRY WHERE FILE_ID = :fileId", nativeQuery = true)
	List<ShapeGeometry> findByFileId(@Param("fileId") int fileId);
	
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM SHP_GEOMETRY WHERE FILE_ID = :fileId AND FILE_UNIQUE_ID IN (:rowNumbers)", nativeQuery = true)
    void deleteRowsByFileIdAndRowNumbers(@Param("fileId") int fileId, @Param("rowNumbers") List<Integer> rowNumbers);
}
