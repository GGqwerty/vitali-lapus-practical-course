package innowise.java.web_store.controller;

import innowise.java.web_store.dto.request.RefreshRequest;
import innowise.java.web_store.dto.request.SignInRequest;
import innowise.java.web_store.dto.request.SignUpRequest;
import innowise.java.web_store.dto.response.TokenResponse;
import innowise.java.web_store.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signIn(@RequestBody SignInRequest req) {
        TokenResponse res = authService.signIn(req);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest req) {
        var res = authService.refresh(req.getRefreshToken());
        return ResponseEntity.ok(res);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<TokenResponse> singUp(@RequestBody SignUpRequest req) {
        TokenResponse token = authService.signUp(req);
        return ResponseEntity.ok(token);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@RequestParam String email) {
        authService.delete(email);
        return ResponseEntity.noContent().build();
    }
}
