package innowise.java.web_store.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentEvent {
    private Long paymentId;
    private Long orderId;
    private String status;
}