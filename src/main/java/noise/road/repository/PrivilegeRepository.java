package noise.road.repository;

import org.springframework.data.repository.CrudRepository;

import noise.road.authenticationModel.Privilege;


public interface PrivilegeRepository extends CrudRepository<Privilege, Integer> {
	Privilege findByName(String name);
}
