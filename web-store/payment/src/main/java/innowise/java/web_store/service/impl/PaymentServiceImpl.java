package innowise.java.web_store.service.impl;

import innowise.java.web_store.dto.request.PaymentRequest;
import innowise.java.web_store.dto.response.PaymentResponse;
import innowise.java.web_store.entity.Payment;
import innowise.java.web_store.mapper.PaymentMapper;
import innowise.java.web_store.repository.PaymentRepository;
import innowise.java.web_store.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final WebClient webClient = WebClient.create("http://www.randomnumberapi.com");

    public PaymentResponse createPayment(PaymentRequest dto) {
        Payment payment = paymentMapper.toEntity(dto);
        payment.setTimestamp(OffsetDateTime.now());

        Integer randomNumber = webClient.get()
                .uri("/api/v1.0/random?min=1&max=100&count=1")
                .retrieve()
                .bodyToMono(Integer[].class)
                .map(arr -> arr[0])
                .block();

        if (randomNumber % 2 == 0) {
            payment.setStatus("SUCCESS");
        } else {
            payment.setStatus("FAILED");
        }

        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toDto(saved);
    }

    public List<PaymentResponse> getPaymentsByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public List<PaymentResponse> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public List<PaymentResponse> getPaymentsByStatuses(List<String> statuses) {
        return paymentRepository.findByStatusIn(statuses)
                .stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    public BigDecimal getTotalSumByPeriod(OffsetDateTime start, OffsetDateTime end) {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getTimestamp() != null)
                .filter(p ->
                        !p.getTimestamp().isBefore(start) &&
                                !p.getTimestamp().isAfter(end)
                )
                .map(Payment::getPaymentAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
