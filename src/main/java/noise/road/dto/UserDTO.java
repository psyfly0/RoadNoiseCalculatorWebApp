package noise.road.dto;

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
	
	@Email(message = "Hibás e-mail formátum")
    private String email;

}
