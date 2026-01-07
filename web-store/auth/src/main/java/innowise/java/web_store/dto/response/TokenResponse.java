package innowise.java.web_store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;

    private String refreshToken;
}
