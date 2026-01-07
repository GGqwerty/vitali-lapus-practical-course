package innowise.java.web_store.kafka.consumer;

import innowise.java.web_store.dto.event.CreateOrderEvent;
import innowise.java.web_store.dto.event.CreatePaymentEvent;
import innowise.java.web_store.dto.request.PaymentRequest;
import innowise.java.web_store.dto.response.PaymentResponse;
import innowise.java.web_store.kafka.producer.PaymentEventProducer;
import innowise.java.web_store.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final PaymentService paymentService;
    private final PaymentEventProducer paymentEventProducer;

    @KafkaListener(topics = "CREATE_ORDER", groupId = "payment-group")
    public void consume(CreateOrderEvent event) {
        PaymentRequest paymentRequest = new PaymentRequest(event.getOrderId(), event.getUserId(), event.getPaymentAmount());

        PaymentResponse payment = paymentService.createPayment(paymentRequest);

        CreatePaymentEvent paymentEvent = new CreatePaymentEvent(payment.getId(), event.getOrderId(), payment.getStatus());
        paymentEventProducer.sendPaymentEvent(paymentEvent);
    }
}
