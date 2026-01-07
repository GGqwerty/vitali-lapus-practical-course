package innowise.java.web_store.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Document(collection = "payment")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    private String id;

    private Long orderId;

    private Long userId;

    private String status;

    @CreatedDate
    private OffsetDateTime timestamp;

    private BigDecimal paymentAmount;
}
