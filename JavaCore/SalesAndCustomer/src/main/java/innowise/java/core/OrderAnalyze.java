package innowise.java.core;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderAnalyze {
    public static Set<String> getUniqueCities(List<Order> orders){
        return orders.stream()
                .map(o -> o.getCustomer().getCity())
                .collect(Collectors.toSet());
    }

    public static double getTotalIncome(List<Order> orders){
        return orders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED)
                .flatMap(o -> o.getItems().stream())
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    public static Optional<String> getMostPopularProduct(List<Order> orders) {
        return orders.stream()
                .flatMap(o -> o.getItems().stream())
                .collect(Collectors.groupingBy(
                        OrderItem::getProductName,
                        Collectors.summingInt(OrderItem::getQuantity)
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }

    public static double getAverageCheck(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED)
                .mapToDouble(o -> o.getItems()
                        .stream()
                        .mapToDouble(i -> i.getPrice() * i.getQuantity())
                        .sum())
                .average()
                .orElse(0.0);
    }

    public static List<Customer> getLoyalCustomers(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(Order::getCustomer, Collectors.counting()))
                .entrySet().stream()
                .filter(e -> e.getValue() > 5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
