package innowise.java.web_store.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
@Data
@Builder
public class UserResponse {

    private Long id;

    private String name;

    private String surname;

    private LocalDate birthDate;

    private String email;
}
