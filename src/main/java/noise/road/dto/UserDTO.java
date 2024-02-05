package noise.road.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
	
	@NotBlank(message = "Felhasználónevet kötelező megadni")
    private String username;
	
	@NotBlank(message = "Jelszót kötelező megadni")
    private String password;
	
	@NotBlank(message = "Jelszót megerősíteni kötelező")
	private String passwordConfirm;
	
	@Email(message = "Hibás e-mail formátum")
    private String email;

	@AssertTrue(message = "Csak angol ábécé karaktereit lehet megadni.")
	public boolean isUsernameValid() {
		return username.matches("^[a-zA-Z0-9]+$");
    }
	
	@AssertTrue(message = "Hibás e-mail formátum")
	public boolean isEmailValid() {
		return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
	}
}
