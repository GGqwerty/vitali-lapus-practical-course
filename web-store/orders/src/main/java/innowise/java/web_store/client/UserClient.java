package innowise.java.web_store.client;

import innowise.java.web_store.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class UserClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${client.user-service-uri}")
    private String userServiceUri;

    public UserResponse getUserByEmail(String email) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(userServiceUri, email)
                    .retrieve()
                    .bodyToMono(UserResponse.class)
                    .block();
        } catch (Exception e) {
            return fallbackUser(email, e);
        }
    }

    private UserResponse fallbackUser(String email, Throwable throwable) {
        return UserResponse.builder()
                .id(null)
                .email(email)
                .name("Unknown")
                .surname("Unknown")
                .build();
    }
}
