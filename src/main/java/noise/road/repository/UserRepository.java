package noise.road.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import noise.road.authenticationModel.User;

public interface UserRepository extends CrudRepository<User, Integer> {
	
	User findByUsername(String username);
	List<User> findAll();
	
}