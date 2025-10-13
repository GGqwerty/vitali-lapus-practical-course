import innowise.java.core.Customer;
import innowise.java.core.Order;
import innowise.java.core.OrderAnalyze;
import innowise.java.core.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderAnalyzeTest {

    private List<Order> orders;

    @BeforeEach
    void setUp() {
        Customer c1 = new Customer("1", "Artem", "artem@mail.ru", LocalDateTime.now(), 25, "Slonim");
        Customer c2 = new Customer("2", "Ilya", "ilya@mail.ru", LocalDateTime.now(), 30, "Kobrin");

        OrderItem item1 = new OrderItem("Smartphone", 1, 1000.0, OrderItem.Category.ELECTRONICS);
        OrderItem item2 = new OrderItem("Book", 2, 20.0, OrderItem.Category.BOOKS);
        OrderItem item3 = new OrderItem("Hat", 3, 15.0, OrderItem.Category.CLOTHING);

        orders = new ArrayList<>();
        orders.add(new Order("1", LocalDateTime.now(), c1, List.of(item1, item2), Order.OrderStatus.DELIVERED));
        orders.add(new Order("2", LocalDateTime.now(), c2, List.of(item2, item3), Order.OrderStatus.CANCELLED));
        orders.add(new Order("3", LocalDateTime.now(), c1, List.of(item3), Order.OrderStatus.DELIVERED));

        for (int i = 4; i < 10; i++) {
            orders.add(new Order(String.valueOf(i), LocalDateTime.now(), c2, List.of(item2), Order.OrderStatus.DELIVERED));
        }
    }

    @Test
    void testUniqueCities() {
        Set<String> cities = OrderAnalyze.getUniqueCities(orders);
        assertEquals(Set.of("Slonim", "Kobrin"), cities);
    }

    @Test
    void testTotalIncome() {
        double total = OrderAnalyze.getTotalIncome(orders);
        assertEquals(1325.0, total, 0.001);
    }

    @Test
    void testMostPopularProduct() {
        Optional<String> product = OrderAnalyze.getMostPopularProduct(orders);
        assertTrue(product.isPresent());
        assertEquals("Book", product.get());
    }

    @Test
    void testAverageCheck() {
        double avg = OrderAnalyze.getAverageCheck(orders);
        assertEquals(165.625, avg, 0.001);
    }

    @Test
    void testLoyalCustomers() {
        List<Customer> loyal = OrderAnalyze.getLoyalCustomers(orders);
        assertEquals(1, loyal.size());
        assertEquals("Ilya", loyal.getFirst().getName());
    }
}
