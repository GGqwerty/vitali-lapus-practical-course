package innowise.java.web_store.service;

import innowise.java.web_store.dto.request.UserRequest;
import innowise.java.web_store.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse create(UserRequest dto);

    UserResponse getById(Long id);

    UserResponse getByEmail(String email);

    Page<UserResponse> getAll(Pageable pageable);

    UserResponse update(Long id, UserRequest dto);

    void delete(Long id);
}
