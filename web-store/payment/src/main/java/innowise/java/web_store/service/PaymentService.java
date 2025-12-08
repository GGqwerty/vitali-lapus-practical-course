package innowise.java.web_store.service;

import innowise.java.web_store.dto.request.PaymentRequest;
import innowise.java.web_store.dto.response.PaymentResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest dto);

    List<PaymentResponse> getPaymentsByOrderId(Long orderId);

    List<PaymentResponse> getPaymentsByUserId(Long userId);

    List<PaymentResponse> getPaymentsByStatuses(List<String> statuses);

    BigDecimal getTotalSumByPeriod(OffsetDateTime start, OffsetDateTime end);
}
