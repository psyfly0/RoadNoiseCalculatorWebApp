package noise.road.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import noise.road.entity.ConstantParameters;

public interface ConstantParametersRepository extends CrudRepository<ConstantParameters, Integer> {

	@Transactional
    @Modifying
    @Query(value = "DELETE FROM CONSTANT_PARAMETERS", nativeQuery = true)
    void deleteAllData();
	
	@Transactional
    @Modifying
    @Query(value = "DROP TABLE CONSTANT_PARAMETERS", nativeQuery = true)
    void dropTable();
	
	@Modifying
	@Query(value = "ALTER TABLE CONSTANT_PARAMETERS ALTER COLUMN ID RESTART WITH 1", nativeQuery = true)
	void resetIdSequence();
	
}
