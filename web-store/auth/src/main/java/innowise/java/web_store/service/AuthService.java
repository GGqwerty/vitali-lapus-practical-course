package innowise.java.web_store.service;

import innowise.java.web_store.dto.request.SignInRequest;
import innowise.java.web_store.dto.request.SignUpRequest;
import innowise.java.web_store.dto.response.TokenResponse;

public interface AuthService {

    TokenResponse signIn(SignInRequest signInRequest);

    TokenResponse signUp(SignUpRequest signUpRequest);

    TokenResponse refresh(String token);
}
