package noise.road.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import noise.road.authenticationModel.UserFileCounter;

public interface UserFileCounterRepository extends CrudRepository<UserFileCounter, Integer> {

	Optional<UserFileCounter> findByUsername(String username);
}
