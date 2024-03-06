package noise.road.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import noise.road.authenticationModel.Privilege;
import noise.road.authenticationModel.Role;
import noise.road.authenticationModel.User;
import noise.road.repository.PrivilegeRepository;
import noise.road.repository.RoleRepository;
import noise.road.repository.UserRepository;
import noise.road.service.TenantService;


@Service
@Slf4j
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PrivilegeRepository privilegeRepository;
    
    @Autowired
    private RoleRepository roleRepository;  
    
    @Autowired
    private TenantService tenantService;
    
    public CustomUserDetails  createUserDetails(User user) {
        return new CustomUserDetails (
                user.getUsername(), 
                user.getPassword(), 
                user.isEnabled(),
                true, 
                true, 
                true, 
                getAuthorities(user.getRoles()),
                user.getEmail()
        );
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public void saveUser(User user) {
    	userRepository.save(user);
    }
    
    public User getUserByUsername(String username) {
    	return userRepository.findByUsername(username);
    }
    
    public User getUserByEmail(String email) {
    	return userRepository.findByEmail(email);
    }
    
    public void deleteUser(User user) {
    	userRepository.delete(user);
    }

    
    public int getMaxGuest() {
    	List<User> guestUsers = userRepository.findByUsernameStartingWith("guest");
    	// Determine the max counter value
        int maxCounter = 0;
        Pattern pattern = Pattern.compile("guest(\\d+)");
        for (User user : guestUsers) {
            Matcher matcher = pattern.matcher(user.getUsername());
            if (matcher.find()) {
                int counter = Integer.parseInt(matcher.group(1));
                maxCounter = Math.max(maxCounter, counter);
            }
        }
    	return maxCounter + 1;
    }

    public User registerNewUser(String username, String password, String email) {
    	User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(new BCryptPasswordEncoder().encode(password));
        newUser.setEmail(email);
        newUser.setEnabled(true); 
    //    log.info("Registering new user: username={}, password={}", username, passwordEncoder.encode(password));

        if (username.startsWith("guest")) {
        	newUser.setRoles(Arrays.asList(roleRepository.findByName("ROLE_GUEST")));
        } else {
        	newUser.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
        }
        
        User savedUser = userRepository.save(newUser);
        tenantService.initDatabase(newUser.getUsername());
        return savedUser;
    }
    
    public String checkUsernameAndEmail(String username, String email) {
        User existingUserWithUsername = userRepository.findByUsername(username);
        User existingUserWithEmail = userRepository.findByEmail(email);

        // Check if username or email is already taken
        if (existingUserWithUsername != null && existingUserWithEmail != null) {
            return "bothExists";
        } else if (existingUserWithUsername != null) {
            return "usernameExists";
        } else if (existingUserWithEmail != null) {
            return "emailExists"; 
        }

        return "noneExists";
    }
      
    @Override
    public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {
 
        User user = userRepository.findByUsername(username);
        if (user == null || !user.isEnabled()) {
        	return new org.springframework.security.core.userdetails.User(
                    " ", " ", true, true, true, true, 
                    getAuthorities(Arrays.asList(
                      roleRepository.findByName("ROLE_USER"))));
        }

        return createUserDetails(user);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(
      Collection<Role> roles) {
 
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(Collection<Role> roles) {
 
        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();
        for (Role role : roles) {
            privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

}

