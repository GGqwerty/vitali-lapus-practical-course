package innowise.java.web_store.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CardInfoResponse {

    private Long id;

    private Long userId;

    private String number;

    private String holder;

    private LocalDate expirationDate;
}
