package noise.road.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import noise.road.authenticationModel.UserFileCounter;

public interface UserFileCounterRepository extends CrudRepository<UserFileCounter, Long> {

	Optional<UserFileCounter> findByUsername(String username);
	
	@Transactional
    @Modifying
    @Query(value = "DELETE FROM USER_FILE_COUNTER", nativeQuery = true)
    void deleteAllData();
	
	@Modifying
	@Query(value = "ALTER TABLE USER_FILE_COUNTER ALTER COLUMN ID RESTART WITH 1", nativeQuery = true)
	void resetIdSequence();
}
