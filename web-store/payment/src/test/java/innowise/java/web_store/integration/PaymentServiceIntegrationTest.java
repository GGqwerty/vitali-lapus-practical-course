package innowise.java.web_store.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import innowise.java.web_store.dto.request.PaymentRequest;
import innowise.java.web_store.dto.response.PaymentResponse;
import innowise.java.web_store.entity.Payment;
import innowise.java.web_store.repository.PaymentRepository;
import innowise.java.web_store.service.PaymentService;
import innowise.java.web_store.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EmbeddedKafka(partitions = 1, topics = {"CREATE_ORDER"}, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    private WireMockServer wireMockServer;

    static MongoDBContainer mongoDB = new MongoDBContainer(DockerImageName.parse("mongo:7.0"));
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        mongoDB.start();
        kafka.start();

        registry.add("spring.data.mongodb.uri", mongoDB::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @BeforeAll
    void setup() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();

        wireMockServer.stubFor(get(urlPathEqualTo("/api/v1.0/random"))
                .willReturn(okJson("[2]")));

        WebClient testClient = WebClient.create(wireMockServer.baseUrl());

        PaymentServiceImpl impl = (PaymentServiceImpl) paymentService;
        ReflectionTestUtils.setField(impl, "webClient", testClient);
    }

    @AfterAll
    void tearDown() {
        wireMockServer.stop();
        mongoDB.stop();
        kafka.stop();
    }

    @Test
    void createPayment_success() {
        PaymentRequest request = new PaymentRequest(123L, 1L, BigDecimal.valueOf(100));
        PaymentResponse response = paymentService.createPayment(request);

        assertEquals("SUCCESS", response.getStatus());

        List<Payment> payments = paymentRepository.findByOrderId(123L);
        assertEquals(1, payments.size());
        assertEquals(BigDecimal.valueOf(100), payments.get(0).getPaymentAmount());
    }
}
