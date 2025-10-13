package innowise.java.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderItem {
    private String productName;
    private int quantity;
    private double price;
    private Category category;

    public enum Category {
        ELECTRONICS, CLOTHING, BOOKS, HOME, BEAUTY, TOYS
    }
}

