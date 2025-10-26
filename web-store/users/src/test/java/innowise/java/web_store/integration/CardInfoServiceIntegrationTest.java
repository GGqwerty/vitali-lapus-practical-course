package innowise.java.web_store.integration;

import innowise.java.web_store.dto.request.CardInfoRequest;
import innowise.java.web_store.dto.response.CardInfoResponse;
import innowise.java.web_store.dto.request.UserRequest;
import innowise.java.web_store.dto.response.UserResponse;
import innowise.java.web_store.service.CardInfoService;
import innowise.java.web_store.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CardInfoServiceIntegrationTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("inno")
            .withUsername("postgres")
            .withPassword("postgres");

    static GenericContainer<?> redis = new GenericContainer<>("redis:7")
            .withExposedPorts(6379)
            .waitingFor(Wait.forListeningPort());

    @Autowired
    private CardInfoService cardInfoService;

    @Autowired
    private UserService userService;

    @DynamicPropertySource
    static void overrideProperties(org.springframework.test.context.DynamicPropertyRegistry registry) {
        postgres.start();
        redis.start();

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @Test
    void createAndGetCard_withCache() {
        UserRequest userReq = new UserRequest();
        userReq.setName("Alice");
        userReq.setSurname("Smith");
        userReq.setEmail("alice@example.com");
        userReq.setBirthDate(LocalDate.of(1990,1,1));
        UserResponse user = userService.create(userReq);

        CardInfoRequest cardReq = new CardInfoRequest();
        cardReq.setUserId(user.getId());
        cardReq.setNumber("1234567890123456");
        cardReq.setHolder("Alice Smith");
        cardReq.setExpirationDate(LocalDate.of(2030,12,31));

        CardInfoResponse card = cardInfoService.create(cardReq);

        CardInfoResponse fromDb = cardInfoService.getById(card.getId());
        assertEquals("Alice Smith", fromDb.getHolder());
    }

    @Test
    void getAllCards_withPagination() {
        UserRequest userReq = new UserRequest();
        userReq.setName("Bob");
        userReq.setSurname("Johnson");
        userReq.setEmail("bob@example.com");
        userReq.setBirthDate(LocalDate.of(1990,1,1));
        UserResponse user = userService.create(userReq);

        for (int i = 0; i < 5; i++) {
            CardInfoRequest cardReq = new CardInfoRequest();
            cardReq.setUserId(user.getId());
            cardReq.setNumber("1234"+i);
            cardReq.setHolder("Bob Johnson");
            cardReq.setExpirationDate(LocalDate.of(2030,12,31));
            cardInfoService.create(cardReq);
        }

        var page = cardInfoService.getAll(PageRequest.of(0, 5));
        assertEquals(5, page.getTotalElements());
    }
}
