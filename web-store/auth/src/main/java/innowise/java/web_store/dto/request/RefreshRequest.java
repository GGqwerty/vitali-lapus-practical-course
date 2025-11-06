package innowise.java.web_store.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshRequest {

    @NotNull
    private String refreshToken;
}
