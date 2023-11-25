package noise.road.repository;

import org.springframework.data.repository.CrudRepository;

import noise.road.authenticationModel.Role;


public interface RoleRepository extends CrudRepository<Role, Integer> {
	Role findByName(String name);
}