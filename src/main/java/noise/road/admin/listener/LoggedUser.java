package noise.road.admin.listener;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import noise.road.admin.model.ActiveUserStore;

@Component
public class LoggedUser implements HttpSessionBindingListener, Serializable {

	private static final long serialVersionUID = 1L;
    private String username; 
    private String email; 
    private ActiveUserStore activeUserStore;
    
    public LoggedUser(String username,String email, ActiveUserStore activeUserStore) {
        this.username = username;
        this.email = email;
        this.activeUserStore = activeUserStore;
    }
    
    public LoggedUser() {}

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
    	List<LoggedUser> loggedUsers = activeUserStore.getLoggedUsers();
        LoggedUser user = (LoggedUser) event.getValue();

        if (!loggedUsers.contains(user)) {
            loggedUsers.add(user);
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
    	List<LoggedUser> loggedUsers = activeUserStore.getLoggedUsers();
        LoggedUser user = (LoggedUser) event.getValue();
        
        loggedUsers.remove(user);
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
