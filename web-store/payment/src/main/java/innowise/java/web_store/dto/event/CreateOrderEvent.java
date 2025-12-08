package innowise.java.web_store.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderEvent {
    private Long orderId;
    private Long userId;
    private BigDecimal paymentAmount;
}
