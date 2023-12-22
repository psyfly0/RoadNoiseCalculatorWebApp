package noise.road.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import noise.road.entity.ShapeGeometry;

public interface ShapeGeometryRepository extends CrudRepository<ShapeGeometry, Integer> {

	@Query(value = "SELECT * FROM SHP_GEOMETRY WHERE FILE_ID = :fileId", nativeQuery = true)
	List<ShapeGeometry> findByFileId(@Param("fileId") int fileId);
}
