package noise.road.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import noise.road.authenticationModel.User;

public interface UserRepository extends CrudRepository<User, Integer> {
	
	User findByUsername(String username);
	User findByEmail(String email);
	List<User> findAll();
	List<User> findByUsernameContainingOrEmailContaining(String username, String email);
}