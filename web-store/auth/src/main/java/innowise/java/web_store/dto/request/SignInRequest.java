package innowise.java.web_store.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInRequest {

    @NotNull
    private String username;

    @NotNull
    private String password;
}
