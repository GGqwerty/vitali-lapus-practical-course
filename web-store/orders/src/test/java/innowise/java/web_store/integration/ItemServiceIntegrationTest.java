package innowise.java.web_store.integration;

import innowise.java.web_store.dto.request.ItemRequest;
import innowise.java.web_store.dto.response.ItemResponse;
import innowise.java.web_store.repository.ItemRepository;
import innowise.java.web_store.service.ItemService;
import org.junit.jupiter.api.*;
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

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemServiceIntegrationTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.4")
            .withDatabaseName("inno")
            .withUsername("postgres")
            .withPassword("postgres")
            .waitingFor(Wait.forListeningPort());

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        postgres.start();

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
    }

    @Test
    void testCreateAndGetItem() {
        ItemRequest request = new ItemRequest();
        request.setName("TestItem");
        request.setPrice(BigDecimal.valueOf(49.99));

        ItemResponse created = itemService.createItem(request);
        assertNotNull(created.getId());
        assertEquals("TestItem", created.getName());

        ItemResponse fromDb = itemService.getItemById(created.getId());
        assertEquals("TestItem", fromDb.getName());
        assertEquals(BigDecimal.valueOf(49.99), fromDb.getPrice());
    }

    @Test
    void testUpdateItem() {
        ItemRequest request = new ItemRequest();
        request.setName("OldItem");
        request.setPrice(BigDecimal.valueOf(10));

        ItemResponse created = itemService.createItem(request);

        ItemRequest update = new ItemRequest();
        update.setName("UpdatedItem");
        update.setPrice(BigDecimal.valueOf(20));

        ItemResponse updated = itemService.updateItem(created.getId(), update);
        assertEquals("UpdatedItem", updated.getName());
        assertEquals(BigDecimal.valueOf(20), updated.getPrice());
    }

    @Test
    void testGetAllItems() {
        for (int i = 0; i < 3; i++) {
            ItemRequest request = new ItemRequest();
            request.setName("Item" + i);
            request.setPrice(BigDecimal.valueOf(10 + i));
            itemService.createItem(request);
        }

        List<ItemResponse> items = itemService.getAllItems();
        assertEquals(3, items.size());
    }

    @Test
    void testDeleteItem() {
        ItemRequest request = new ItemRequest();
        request.setName("DeleteMe");
        request.setPrice(BigDecimal.valueOf(5));

        ItemResponse created = itemService.createItem(request);
        itemService.deleteItem(created.getId());

        assertFalse(itemRepository.existsById(created.getId()));
    }
}
