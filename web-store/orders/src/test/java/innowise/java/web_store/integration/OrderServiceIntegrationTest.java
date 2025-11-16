package innowise.java.web_store.integration;

import innowise.java.web_store.client.UserClient;
import innowise.java.web_store.dto.request.OrderItemRequest;
import innowise.java.web_store.dto.request.OrderRequest;
import innowise.java.web_store.dto.response.OrderResponse;
import innowise.java.web_store.dto.response.UserResponse;
import innowise.java.web_store.entity.Item;
import innowise.java.web_store.repository.ItemRepository;
import innowise.java.web_store.repository.OrderRepository;
import innowise.java.web_store.service.OrderService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderServiceIntegrationTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.4")
            .withDatabaseName("inno")
            .withUsername("postgres")
            .withPassword("postgres")
            .waitingFor(Wait.forListeningPort());

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    private UserClient userClient;

    private Item testItem;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        postgres.start();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        itemRepository.deleteAll();

        userClient = Mockito.mock(UserClient.class);
        Mockito.when(userClient.getUserByEmail(Mockito.anyString()))
                .thenAnswer(invocation -> {
                    UserResponse user = new UserResponse();
                    user.setEmail("mail@mail.mail");
                    user.setName("John");
                    user.setSurname("Doe");
                    return user;
                });

        testItem = new Item();
        testItem.setName("Item1");
        testItem.setPrice(BigDecimal.valueOf(50));
        testItem = itemRepository.save(testItem);

        UserResponse mockUser = new UserResponse();
        mockUser.setEmail("mail@mail.mail");
        mockUser.setName("John");
        mockUser.setSurname("Doe");

        when(userClient.getUserByEmail(anyString())).thenReturn(mockUser);
    }

    @Test
    void testCreateOrder() {
        OrderRequest request = new OrderRequest();
        request.setUserId("mail@mail.mail");
        request.setItems(List.of(new OrderItemRequest(testItem.getId(), 4)));
        request.setStatus("NEW");

        OrderResponse created = orderService.createOrder(request);

        assertNotNull(created.getId());
        assertEquals(1, created.getItems().size());
        assertEquals("Item1", created.getItems().get(0).getName());
        assertEquals("mail@mail.mail", created.getUser().getEmail());
    }

    @Test
    void testGetOrderById() {
        OrderRequest request = new OrderRequest();
        request.setUserId("mail@mail.mail");
        request.setItems(List.of(new OrderItemRequest(testItem.getId(), 4)));
        request.setStatus("NEW");
        OrderResponse created = orderService.createOrder(request);

        OrderResponse fetched = orderService.getOrderById(created.getId());
        assertEquals(created.getId(), fetched.getId());
        assertEquals("mail@mail.mail", fetched.getUser().getEmail());
    }

    @Test
    void testGetOrdersByIds() {
        OrderRequest request1 = new OrderRequest();
        request1.setUserId("mail@mail.mail");
        request1.setItems(List.of(new OrderItemRequest(testItem.getId(), 4)));
        request1.setStatus("NEW");
        OrderResponse order1 = orderService.createOrder(request1);

        OrderRequest request2 = new OrderRequest();
        request2.setUserId("mail2@mail.mail");
        request2.setItems(List.of());
        request2.setStatus("NEW");
        OrderResponse order2 = orderService.createOrder(request2);

        List<OrderResponse> orders = orderService.getOrdersByIds(List.of(order1.getId(), order2.getId()));
        assertEquals(2, orders.size());
        assertEquals("mail@mail.mail", orders.get(0).getUser().getEmail());
    }

    @Test
    void testGetOrdersByStatuses() {
        OrderRequest request = new OrderRequest();
        request.setUserId("mail@mail.mail");
        request.setItems(List.of(new OrderItemRequest(testItem.getId(), 4)));
        request.setStatus("NEW");
        OrderResponse created = orderService.createOrder(request);

        List<OrderResponse> orders = orderService.getOrdersByStatuses(List.of(created.getStatus()));
        assertFalse(orders.isEmpty());
        assertEquals("mail@mail.mail", orders.get(0).getUser().getEmail());
    }

    @Test
    void testGetOrdersByUserId() {
        OrderRequest request = new OrderRequest();
        request.setUserId("mail@mail.mail");
        request.setItems(List.of(new OrderItemRequest(testItem.getId(), 4)));
        request.setStatus("NEW");
        OrderResponse created = orderService.createOrder(request);

        List<OrderResponse> orders = orderService.getOrdersByUserId("mail@mail.mail");
        assertFalse(orders.isEmpty());
        assertEquals("mail@mail.mail", orders.get(0).getUser().getEmail());
    }

    @Test
    void testUpdateOrder() {
        OrderRequest request = new OrderRequest();
        request.setUserId("mail@mail.mail");
        request.setStatus("NEW");
        request.setItems(List.of(new OrderItemRequest(testItem.getId(), 4)));
        OrderResponse created = orderService.createOrder(request);

        OrderRequest update = new OrderRequest();
        update.setUserId("mail@mail.mail");
        update.setItems(List.of());
        OrderResponse updated = orderService.updateOrder(created.getId(), update);

        assertTrue(updated.getItems().isEmpty());
        assertEquals("mail@mail.mail", updated.getUser().getEmail());
    }

    @Test
    void testDeleteOrder() {
        OrderRequest request = new OrderRequest();
        request.setUserId("mail@mail.mail");
        request.setStatus("NEW");
        request.setItems(List.of(new OrderItemRequest(testItem.getId(), 4)));
        OrderResponse created = orderService.createOrder(request);

        orderService.deleteOrder(created.getId());
        assertFalse(orderRepository.existsById(created.getId()));
    }
}
