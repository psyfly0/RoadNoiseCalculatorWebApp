package noise.road.admin.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import noise.road.admin.listener.LoggedUser;

@Data
public class ActiveUserStore {

	public List<LoggedUser> loggedUsers;

	public ActiveUserStore() {
		loggedUsers = new ArrayList<>();
    }
	
}
