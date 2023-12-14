package noise.road.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import noise.road.entity.MutableParameters;

public interface MutableParametersRepository extends JpaRepository<MutableParameters, Integer> {
	
	@Query(value = "SELECT * FROM MUTABLE_PARAMETERS WHERE FILE_ID = :fileId", nativeQuery = true)
	List<MutableParameters> findByFileId(@Param("fileId") int fileId);
	

}
