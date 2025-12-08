package innowise.java.web_store.kafka.producer;

import innowise.java.web_store.dto.event.CreatePaymentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, CreatePaymentEvent> kafkaTemplate;

    private static final String TOPIC = "CREATE_PAYMENT";

    public void sendPaymentEvent(CreatePaymentEvent event) {
        kafkaTemplate.send(TOPIC, event.getPaymentId().toString(), event);
    }
}

