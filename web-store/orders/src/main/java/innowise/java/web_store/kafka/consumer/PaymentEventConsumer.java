package innowise.java.web_store.kafka.consumer;

import innowise.java.web_store.dto.event.CreatePaymentEvent;
import innowise.java.web_store.dto.request.OrderRequest;
import innowise.java.web_store.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = "CREATE_PAYMENT", groupId = "order-group")
public class PaymentEventConsumer {

    private final OrderService orderService;

    @KafkaHandler
    public void consume(CreatePaymentEvent event) {
        orderService.updateOrderStatus(event.getOrderId(), event.getStatus());
    }
}

