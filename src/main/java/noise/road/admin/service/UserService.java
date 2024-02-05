package noise.road.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import noise.road.authenticationModel.Role;
import noise.road.authenticationModel.User;
import noise.road.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;

	public List<User> getAllUsers() {
	//	return userRepository.findAll();
		List<User> allUsers = userRepository.findAll();

        // Filter out users with the "GUEST" role
        return allUsers.stream()
                .filter(user -> user.getRoles().stream().noneMatch(role -> "ROLE_GUEST".equals(role.getName())))
                .collect(Collectors.toList());
    
	}
	
	public List<User> findByUsernameContainingOrEmailContaining(String username, String email) {
		return userRepository.findByUsernameContainingOrEmailContaining(username, email);
	}
	
	public User getUserById(Integer userId) {
		return userRepository.findById(userId).orElse(null);
	}
	
	@Transactional
	public void updateUser(User modifiedUser) {
		userRepository.findById(modifiedUser.getId()).ifPresent(existingUser -> {
            existingUser.setUsername(modifiedUser.getUsername());
            existingUser.setEnabled(modifiedUser.isEnabled());

            userRepository.save(existingUser);
        });
	}
	
	@Transactional
	public void updateSchemaName(String oldName, String newName) {
		jdbcTemplate.execute("ALTER SCHEMA IF EXISTS \"" + oldName + "\" RENAME TO \"" + newName + "\"");
	}
	
	@Transactional
	public void deleteUser(Integer userId, String username) {
		userRepository.deleteById(userId);
		jdbcTemplate.execute("DROP SCHEMA IF EXISTS \"" + username + "\" CASCADE");
	}
}
