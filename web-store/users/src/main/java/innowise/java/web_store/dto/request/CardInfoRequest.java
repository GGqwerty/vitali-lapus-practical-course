package innowise.java.web_store.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CardInfoRequest {

    @NotNull
    private Long userId;

    @NotBlank
    @Size(max = 20)
    private String number;

    @NotBlank
    @Size(max = 100)
    private String holder;

    @NotNull
    private LocalDate expirationDate;
}
