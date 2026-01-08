package innowise.java.web_store.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import innowise.java.web_store.dto.request.SignUpRequest;
import innowise.java.web_store.dto.response.TokenResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class SignUpFilter implements GatewayFilter {

    private final ObjectMapper objectMapper;

    @Value("${services.user-service-url}")
    private String userServiceUrl;

    @Value("${services.auth-service-url}")
    private String authServiceUrl;

    private WebClient authClient;
    private WebClient userClient;

    @PostConstruct
    private void initClients() {
        authClient = WebClient.create(authServiceUrl);
        userClient = WebClient.create(userServiceUrl);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().value();

        if (!path.equals("/api/auth/sign-up")) {
            return chain.filter(exchange);
        }

        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);

                    SignUpRequest request;
                    try {
                        request = objectMapper.readValue(bytes, SignUpRequest.class);
                    } catch (Exception e) {
                        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                        return exchange.getResponse().setComplete();
                    }

                    return processSignUp(exchange, request);
                });
    }

    private Mono<Void> processSignUp(ServerWebExchange exchange, SignUpRequest req) {

        return authClient.post()
                .uri("/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .flatMap(tokenResponse ->
                        userClient.post()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(req)
                                .retrieve()
                                .toBodilessEntity()

                                .flatMap(
                                        ignored -> respond(exchange, tokenResponse))

                                .onErrorResume(err ->
                                        rollbackAuthAndFail(exchange, req.getEmail())
                                )
                )

                .onErrorResume(err -> {
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
    }

    private Mono<Void> rollbackAuthAndFail(ServerWebExchange exchange, String email) {

        return authClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/delete")
                        .queryParam("email", email)
                        .build()
                )
                .retrieve()
                .bodyToMono(Void.class)
                .then(Mono.defer(() -> {
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    byte[] msg = "User creation failed. Rolled back auth.".getBytes();
                    DataBuffer body = exchange.getResponse()
                            .bufferFactory()
                            .wrap(msg);
                    return exchange.getResponse().writeWith(Mono.just(body));
                }));
    }

    private Mono<Void> respond(ServerWebExchange exchange, Object bodyObj) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(bodyObj);

            exchange.getResponse().setStatusCode(HttpStatus.CREATED);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

            DataBuffer buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(json);

            return exchange.getResponse().writeWith(Mono.just(buffer));

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return exchange.getResponse().setComplete();
        }
    }
}