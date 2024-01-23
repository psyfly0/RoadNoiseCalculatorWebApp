package noise.road.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import noise.road.authenticationModel.Privilege;
import noise.road.authenticationModel.Role;
import noise.road.authenticationModel.User;
import noise.road.repository.PrivilegeRepository;
import noise.road.repository.RoleRepository;
import noise.road.repository.UserRepository;



@Component
public class SetupDataLoader implements
  ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;
 
    @Autowired
    private RoleRepository roleRepository;
 
    @Autowired
    private PrivilegeRepository privilegeRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
 
  //  @Autowired
   // private PasswordEncoder passwordEncoder;
 
    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
 
        if (alreadySetup)
            return;
        Privilege readPrivilege
          = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege
          = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        Privilege guestPrivilege 
		  = createPrivilegeIfNotFound("GUEST_PRIVILEGE");
 
        List<Privilege> adminPrivileges = Arrays.asList(
          readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));
        createRoleIfNotFound("ROLE_GUEST", Arrays.asList(guestPrivilege));

        if (userRepository.findByUsername("admin") == null) {
	        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
	        User adminUser = new User();
	        adminUser.setPassword(new BCryptPasswordEncoder().encode("1"));
	        adminUser.setUsername("admin");
	        adminUser.setEnabled(true);
	        adminUser.setRoles(Arrays.asList(adminRole));
	        userRepository.save(adminUser);
        }

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {
 
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(
      String name, Collection<Privilege> privileges) {
 
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
}
