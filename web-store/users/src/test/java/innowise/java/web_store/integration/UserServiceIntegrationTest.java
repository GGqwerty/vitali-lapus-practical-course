package innowise.java.web_store.integration;

import innowise.java.web_store.dto.request.UserRequest;
import innowise.java.web_store.dto.response.UserResponse;
import innowise.java.web_store.repository.UserRepository;
import innowise.java.web_store.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceIntegrationTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("inno")
            .withUsername("postgres")
            .withPassword("postgres");

    static GenericContainer<?> redis = new GenericContainer<>("redis:7")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        postgres.start();
        redis.start();

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createAndGetUser_withCache() {
        UserRequest request = new UserRequest();
        request.setName("John");
        request.setSurname("Doe");
        request.setEmail("john@example.com");
        request.setBirthDate(LocalDate.of(1990, 1, 1));

        UserResponse created = userService.create(request);

        UserResponse fromDb = userService.getById(created.getId());
        assertEquals("John", fromDb.getName());

        UserResponse fromCache = userService.getByEmail("john@example.com");
        assertEquals("John", fromCache.getName());
    }

    @Test
    void getAllUsers_withPagination() {
        for (int i = 0; i < 5; i++) {
            UserRequest request = new UserRequest();
            request.setName("Name" + i);
            request.setSurname("Surname" + i);
            request.setEmail("email" + i + "@example.com");
            request.setBirthDate(LocalDate.of(1990,1,1));
            userService.create(request);
        }

        Pageable pageable = PageRequest.of(0, 5, Sort.by("surname").ascending().and(Sort.by("name").ascending()));
        Page<UserResponse> page = userService.getAll(pageable);

        assertEquals(5, page.getTotalElements());
    }

    @Test
    void updateAndDeleteUser() {
        UserRequest request = new UserRequest();
        request.setName("Jane");
        request.setSurname("Doe");
        request.setEmail("jane@example.com");
        request.setBirthDate(LocalDate.of(1990,1,1));

        UserResponse created = userService.create(request);

        UserRequest update = new UserRequest();
        update.setName("Janet");
        UserResponse updated = userService.update(created.getId(), update);
        assertEquals("Janet", updated.getName());

        userService.delete(created.getId());
        assertThrows(Exception.class, () -> userService.getById(created.getId()));
    }
}