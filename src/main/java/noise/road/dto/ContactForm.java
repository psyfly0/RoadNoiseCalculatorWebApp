package noise.road.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactForm {
	
	private String name;
    private String email;
    private String subject;
    private String message;

}
