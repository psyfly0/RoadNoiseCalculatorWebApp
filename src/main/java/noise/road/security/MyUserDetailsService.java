package noise.road.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import noise.road.authenticationModel.Privilege;
import noise.road.authenticationModel.Role;
import noise.road.authenticationModel.User;
import noise.road.repository.PrivilegeRepository;
import noise.road.repository.RoleRepository;
import noise.road.repository.UserRepository;


@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PrivilegeRepository privilegeRepository;
    
    @Autowired
    private RoleRepository roleRepository;  
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public UserDetails createUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), 
                user.getPassword(), 
                user.isEnabled(), 
                true, 
                true, 
                true, 
                getAuthorities(user.getRoles())
        );
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User registerNewUser(String username, String password, String email) {
    	User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEmail(email);
        newUser.setEnabled(true); 

        newUser.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER")));
        return userRepository.save(newUser);
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
        if (user == null) {
        	return new org.springframework.security.core.userdetails.User(
                    " ", " ", true, true, true, true, 
                    getAuthorities(Arrays.asList(
                      roleRepository.findByName("ROLE_USER"))));
        }

        return new org.springframework.security.core.userdetails.User(
          user.getUsername(), user.getPassword(), user.isEnabled(), true, true, 
          true, getAuthorities(user.getRoles()));
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

