package innowise.java.web_store.kafka.producer;

import innowise.java.web_store.dto.event.CreateOrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, CreateOrderEvent> kafkaTemplate;

    private static final String TOPIC = "CREATE_ORDER";

    public void sendOrderEvent(CreateOrderEvent event) {
        kafkaTemplate.send(TOPIC, event.getOrderId().toString(), event);
    }
}
