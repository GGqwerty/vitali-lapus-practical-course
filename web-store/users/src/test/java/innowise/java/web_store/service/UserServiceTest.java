package innowise.java.web_store.service;

import innowise.java.web_store.entity.User;
import innowise.java.web_store.repository.UserRepository;
import innowise.java.web_store.dto.request.UserRequest;
import innowise.java.web_store.dto.response.UserResponse;
import innowise.java.web_store.exception.ApiException;
import innowise.java.web_store.mapper.UserMapper;
import innowise.java.web_store.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserById_found() {
        User entity = new User();
        entity.setId(1L);
        entity.setEmail("test@example.com");

        UserResponse dto = new UserResponse();
        dto.setId(1L);
        dto.setEmail("test@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(userMapper.toDTO(entity)).thenReturn(dto);

        UserResponse result = userService.getById(1L);
        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> userService.getById(1L));
    }

    @Test
    void getUserByEmail_found() {
        User entity = new User();
        entity.setId(2L);
        entity.setEmail("email@example.com");

        UserResponse dto = new UserResponse();
        dto.setId(2L);
        dto.setEmail("email@example.com");

        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(entity));
        when(userMapper.toDTO(entity)).thenReturn(dto);

        UserResponse result = userService.getByEmail("email@example.com");
        assertEquals("email@example.com", result.getEmail());
    }

    @Test
    void getUserByEmail_notFound() {
        when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> userService.getByEmail("email@example.com"));
    }

    @Test
    void getAllUsers_withPagination() {
        User e1 = new User();
        e1.setId(1L);
        User e2 = new User();
        e2.setId(2L);

        List<User> list = Arrays.asList(e1, e2);
        Pageable pageable = PageRequest.of(0, 2, Sort.by("surname").ascending().and(Sort.by("name").ascending()));
        Page<User> page = new PageImpl<>(list, pageable, list.size());

        when(userRepository.findAllByOrderBySurnameAscNameAsc(pageable)).thenReturn(page);
        when(userMapper.toDTO(e1)).thenReturn(new UserResponse(){ { setId(1L); } });
        when(userMapper.toDTO(e2)).thenReturn(new UserResponse(){ { setId(2L); } });

        Page<UserResponse> result = userService.getAll(pageable);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void createUser_success() {
        UserRequest request = new UserRequest();
        request.setName("John");
        request.setSurname("Doe");
        request.setEmail("john@example.com");
        request.setBirthDate(LocalDate.of(1990,1,1));

        User entity = new User();
        entity.setId(1L);

        UserResponse response = new UserResponse();
        response.setId(1L);

        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.toDTO(entity)).thenReturn(response);

        UserResponse result = userService.create(request);
        assertEquals(1L, result.getId());
    }

    @Test
    void updateUser_success() {
        UserRequest request = new UserRequest();
        request.setName("NewName");

        User entity = new User();
        entity.setId(1L);

        UserResponse response = new UserResponse();
        response.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(userMapper).updateEntityFromDTO(request, entity);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.toDTO(entity)).thenReturn(response);

        UserResponse result = userService.update(1L, request);
        assertEquals(1L, result.getId());
    }

    @Test
    void updateUser_notFound() {
        UserRequest request = new UserRequest();
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> userService.update(1L, request));
    }

    @Test
    void deleteUser_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteByIdJPQL(1L);
        assertDoesNotThrow(() -> userService.delete(1L));
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(ApiException.class, () -> userService.delete(1L));
    }
}
