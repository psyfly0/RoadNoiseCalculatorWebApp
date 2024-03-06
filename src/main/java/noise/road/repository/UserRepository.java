package noise.road.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import noise.road.authenticationModel.User;

public interface UserRepository extends CrudRepository<User, Integer> {
	
	User findByUsername(String username);
	User findByEmail(String email);
	List<User> findAll();
	List<User> findByUsernameContainingOrEmailContaining(String username, String email);
	List<User> findByUsernameStartingWith(String prefix);

	@Query(value = "SELECT ID FROM \"admin\".USERS WHERE USERNAME = :username", nativeQuery = true)
    Integer findUserIdByUsername(@Param("username") String username);
	
	@Transactional
    @Modifying
	@Query(value = "DELETE FROM \"admin\".USERS WHERE ID = :guestId", nativeQuery = true)
	void deleteGuestFromUSERS(@Param("guestId") Integer guestId);
	
	@Transactional
    @Modifying
	@Query(value = "DELETE FROM \"admin\".USERS_ROLES WHERE USER_ID = :guestId", nativeQuery = true)
	void deleteGuestFromUSERS_ROLES(@Param("guestId") Integer guestId);
}