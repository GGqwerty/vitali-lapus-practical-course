package innowise.java.web_store.service;

import innowise.java.web_store.dto.request.PaymentRequest;
import innowise.java.web_store.dto.response.PaymentResponse;
import innowise.java.web_store.entity.Payment;
import innowise.java.web_store.mapper.PaymentMapper;
import innowise.java.web_store.repository.PaymentRepository;
import innowise.java.web_store.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        paymentService = new PaymentServiceImpl(paymentRepository, paymentMapper);
    }

    @Test
    void createPayment_successful() {
        PaymentRequest request = new PaymentRequest(1L, 1L, BigDecimal.valueOf(100));

        Payment paymentEntity = new Payment();
        paymentEntity.setOrderId(1L);
        paymentEntity.setUserId(1L);
        paymentEntity.setPaymentAmount(BigDecimal.valueOf(100));

        Payment savedPayment = new Payment();
        savedPayment.setId("p1");
        savedPayment.setOrderId(1L);
        savedPayment.setUserId(1L);
        savedPayment.setPaymentAmount(BigDecimal.valueOf(100));
        savedPayment.setStatus("SUCCESS");
        savedPayment.setTimestamp(Instant.now());

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setId("p1");
        paymentResponse.setStatus("SUCCESS");

        when(paymentMapper.toEntity(request)).thenReturn(paymentEntity);
        when(paymentMapper.toDto(savedPayment)).thenReturn(paymentResponse);
        
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Integer[].class)).thenReturn(Mono.just(new Integer[]{2}));

        PaymentResponse result = paymentService.createPayment(request);

        assertNotNull(result);
        assertEquals("SUCCESS", result.getStatus());

        verify(paymentRepository, times(1)).save(paymentEntity);
        verify(paymentMapper, times(1)).toDto(savedPayment);
    }

    @Test
    void createPayment_failed() {
        PaymentRequest request = new PaymentRequest(2L, 2L, BigDecimal.valueOf(50));

        Payment paymentEntity = new Payment();
        paymentEntity.setOrderId(2L);
        paymentEntity.setUserId(2L);
        paymentEntity.setPaymentAmount(BigDecimal.valueOf(50));

        Payment savedPayment = new Payment();
        savedPayment.setId("p2");
        savedPayment.setOrderId(2L);
        savedPayment.setUserId(2L);
        savedPayment.setPaymentAmount(BigDecimal.valueOf(50));
        savedPayment.setStatus("FAILED");
        savedPayment.setTimestamp(Instant.now());

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setId("p2");
        paymentResponse.setStatus("FAILED");

        when(paymentMapper.toEntity(request)).thenReturn(paymentEntity);
        when(paymentMapper.toDto(savedPayment)).thenReturn(paymentResponse);
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Integer[].class)).thenReturn(Mono.just(new Integer[]{3}));

        PaymentResponse result = paymentService.createPayment(request);

        assertNotNull(result);
        assertEquals("FAILED", result.getStatus());
    }

    @Test
    void getPaymentsByOrderId_returnsList() {
        Payment payment = new Payment();
        payment.setOrderId(1L);

        PaymentResponse response = new PaymentResponse();
        response.setId("p1");

        when(paymentRepository.findByOrderId(1L)).thenReturn(List.of(payment));
        when(paymentMapper.toDto(payment)).thenReturn(response);

        List<PaymentResponse> result = paymentService.getPaymentsByOrderId("1");

        assertEquals(1, result.size());
        assertEquals("p1", result.get(0).getId());
    }

    @Test
    void getTotalSumByPeriod_calculatesCorrectly() {
        Payment p1 = new Payment();
        p1.setPaymentAmount(BigDecimal.valueOf(100));
        p1.setTimestamp(OffsetDateTime.now().minusDays(1).toInstant());

        Payment p2 = new Payment();
        p2.setPaymentAmount(BigDecimal.valueOf(50));
        p2.setTimestamp(Instant.now());

        when(paymentRepository.findAll()).thenReturn(List.of(p1, p2));

        OffsetDateTime start = OffsetDateTime.now().minusDays(2);
        OffsetDateTime end = OffsetDateTime.now().plusDays(1);

        BigDecimal total = paymentService.getTotalSumByPeriod(start, end);

        assertEquals(BigDecimal.valueOf(150), total);
    }
}
